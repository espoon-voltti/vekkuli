# Design — Prevent composing emails that exceed AWS SES 10MB limit

**Status:** Approved (design phase)
**Date:** 2026-05-22

## Problem

AWS SES rejects messages whose final MIME size exceeds 10MB. The mass-email composer in this app currently lets users attach files freely; each file is capped at 10MB individually, but there is no check on the *combined* size of body + attachments. When the user clicks Lähetä, `SendEmailService.sendEmail` calls `sendRawEmail`, SES throws `SesException`, and the message is silently lost (the exception is logged but the user-facing flow does not distinguish size errors from other failures).

We need to prevent the user from ever creating a draft whose combined size exceeds the SES limit.

## Goals

1. The user cannot upload an attachment that would push the draft's combined size over the SES limit.
2. The error surfaces in the composer UI at the moment of the offending upload, in Finnish, with enough information for the user to recover (delete an attachment or pick a smaller file).
3. The change applies to *every* email flow that uses `SendEmailService` (mass and any single-recipient flows sharing the attachment system).

## Non-goals

- Showing a real-time running total in the UI before the user uploads.
- Re-checking the limit at send time in `SendEmailService` (deliberately omitted — see "Accepted risks").
- Backfilling sizes for existing in-flight attachment rows from S3.
- Reducing the per-file 10MB ceiling.

## Decisions made during brainstorming

| Decision | Choice | Why |
|---|---|---|
| Enforcement point | **Only at upload** | Body text in this app is short; combined check on the attachment-add path catches all realistic overflows. Skipping a send-time recheck keeps `SendEmailService` unchanged. |
| Limit math | **Cap raw attachment bytes around 7MB** | Base64 inflates binary by ×4/3; capping raw at 7MB gives ≈9.33MB encoded, leaving ~670KB headroom for body + MIME headers under SES's 10MB. |
| Scope | **All flows using `SendEmailService`** | Same composer and attachment endpoints serve mass and individual emails; consistent behavior, less special-casing. |
| Running-total approach | **A — client posts existing attachment IDs along with the new file** | Matches the existing pattern where hidden `attachmentId` inputs in the form are posted on send; keeps upload stateless; one new column (`size_bytes`) on the `attachment` table. |

## Architecture summary

The HTMX upload form already keeps each attached file as a hidden `<input name="attachmentId" value="...">` inside the composer. Today, those IDs only travel to the server when the user clicks Lähetä. We change the upload form so that on every `POST /virkailija/viestit/lisaa-liite` the existing attachment IDs ride along (`hx-include`).

The server reads those IDs, looks up their stored `size_bytes`, sums them with the new file's size, and rejects the upload if the sum exceeds the combined raw cap. On rejection it returns `422 Unprocessable Entity` with a small HTML fragment that HTMX swaps into a dedicated error region. On success it stores the new attachment's size for future running-total computations.

## Sizing math

- SES message limit: **10,000,000 bytes** (using the project's existing decimal-MB convention, matching `AwsConstants.MAX_FILE_SIZE = 10 * 1000 * 1000`).
- Base64 encoding inflates binary by exactly 4/3, plus ~2.6% CRLF line-wrap overhead per RFC 2045.
- Combined-raw cap: **`MAX_RAW_ATTACHMENT_TOTAL_BYTES = 7 * 1000 * 1000`**
  - 7,000,000 B raw → ≈ 9,333,333 B base64-encoded
  - Leaves ~670KB for the body, subject, MIME headers, and per-part boundaries before SES's 10MB ceiling.
  - Body text in mass emails in this app is short text (typically much less than 100KB), so the margin is comfortable.

## Data model changes

### Flyway migration `V024__add_size_to_attachments.sql`

```sql
ALTER TABLE attachment
  ADD COLUMN size_bytes BIGINT;
```

- Nullable. Existing rows stay NULL.
- New rows always populated from `MultipartFile.size` (always non-null at upload time).
- The service treats NULL conservatively (see service layer) so no backfill job is required.

### Domain `Attachment` (in `QueuedMessage.kt`)

Add `sizeBytes: Long?` (nullable to match the DB).

### `AttachmentRepository`

- Update the row mapper to read `size_bytes`.
- `addAttachment(key, name)` becomes `addAttachment(key, name, sizeBytes)`.
- Add `findSizesByIds(ids: List<UUID>): Map<UUID, Long?>` returning a map of attachment ID → stored size (NULL preserved). This is the single bulk read used by the service.

## Service layer

`AttachmentService.uploadAttachment(...)`:

```kotlin
fun uploadAttachment(
    contentType: String?,
    input: InputStream,
    size: Long,
    name: String,
    existingAttachmentIds: List<UUID>,   // NEW
): UUID
```

Order of checks (failing earlier = clearer error):

1. Existing validations: content type non-null, allowed content type, size ≥ 0.
2. **Per-file check (kept):** `size > AwsConstants.MAX_FILE_SIZE` → throw `IllegalArgumentException("File size exceeds maximum allowed size")`. This stays so a single-file overflow has its own unambiguous error.
3. **New combined check:**
   - Fetch sizes for `existingAttachmentIds` via `findSizesByIds`.
   - For each ID returned, use the stored size, or `AwsConstants.MAX_FILE_SIZE` (conservative upper bound) if NULL.
   - `projectedRaw = existingSizesSum + size`
   - If `projectedRaw > AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES`, throw `MessageSizeLimitExceededException(currentBytes = existingSizesSum, attemptedBytes = size, limitBytes = MAX_RAW_ATTACHMENT_TOTAL_BYTES)`.
4. Existing S3 upload path.
5. `attachmentRepository.addAttachment(key, name, sizeBytes = size)` — store the size.

### New exception type

```kotlin
class MessageSizeLimitExceededException(
    val currentBytes: Long,
    val attemptedBytes: Long,
    val limitBytes: Long,
) : RuntimeException("Attachment combined size limit exceeded")
```

Lives next to `AttachmentService` (same package). Carries the three numbers so the controller can render a useful message without re-computing.

### Constants (`object AwsConstants` in `service/src/main/kotlin/fi/espoo/vekkuli/config/AwsConfig.kt`)

Add:

```kotlin
/** Raw (pre-MIME-encoded) combined size cap for all attachments on a single email.
 *  Chosen so that base64-encoded MIME size stays safely under the SES 10MB limit
 *  with headroom for body and headers. */
const val MAX_RAW_ATTACHMENT_TOTAL_BYTES: Long = 7L * 1000 * 1000
```

Keep `MAX_FILE_SIZE` unchanged.

## Controller

`AttachmentController.addAttachment`:

- New request param: `@RequestParam("attachmentId") existingAttachmentIds: List<UUID> = emptyList()`
- Pass through to `attachmentService.uploadAttachment(..., existingAttachmentIds = existingAttachmentIds)`
- New catch branch for `MessageSizeLimitExceededException`:
  - Return `HttpStatus.UNPROCESSABLE_ENTITY` (422)
  - Body is a small HTML fragment rendered by `AttachmentView.renderSizeLimitError(currentBytes, attemptedBytes, limitBytes)` — a Finnish-language message naming the limit in megabytes and telling the user to remove an attachment or pick a smaller file.
- Existing generic `catch (e: Exception)` returning 400 stays for everything else.

The new exception is mapped before the generic catch.

## View

`AttachmentView.render()` (`boatSpace/employeeReservationList/components/AttachmentView.kt`):

1. Add `hx-include="#attachment-list input[name='attachmentId']"` so existing attachment IDs are posted on every upload. The selector is document-root because the upload `<form>` and `<ul id="attachment-list">` are siblings in the rendered markup (verified in current `AttachmentView.render()`).

2. Replace the single generic `<div id="error-box">` with a dedicated `<div id="attachment-error">` plus the existing generic error div. Update the `@htmx:after-request` handler to:
   - On 422: do nothing extra (server-rendered fragment already targets `#attachment-error`).
   - On other failure (4xx/5xx): show the generic error box as today.
   - On success: clear both error regions.

3. Add a new method `AttachmentView.renderSizeLimitError(currentBytes, attemptedBytes, limitBytes): String` returning a Finnish-language fragment along the lines of:
   > "Liitteiden yhteenlaskettu koko ylittäisi sallitun rajan ({current+attempted} MB / {limit} MB). Poista liite tai valitse pienempi tiedosto."

   Sizes formatted to one-decimal megabytes (decimal MB, matching the `1000 * 1000` byte convention used elsewhere in this project). Exact wording can be tuned during implementation; the message must name both the offending combined size and the limit, and tell the user how to recover.

## Tests

Add to existing test suites for `AttachmentService` and `AttachmentController`:

- Service: combined size **just under** limit → succeeds, `size_bytes` persisted with the exact uploaded size.
- Service: combined size **exactly at** limit → succeeds (boundary is inclusive of the cap).
- Service: combined size **just over** limit → throws `MessageSizeLimitExceededException`; S3 `putObject` not invoked; no DB row inserted.
- Service: single file > `MAX_FILE_SIZE` → per-file `IllegalArgumentException` wins regardless of existing total.
- Service: `existingAttachmentIds = emptyList()` → check uses 0 baseline and only validates the new file.
- Service: one of the existing IDs has `size_bytes IS NULL` → that ID contributes `MAX_FILE_SIZE` to the sum (conservative fallback).
- Service: an existing ID is unknown (not in DB) → treated as 0 in the sum (the row is gone, so it can't be referenced at send time).
- Controller: over-limit upload returns HTTP 422 with the error fragment; HTMX swap target is the dedicated error region.
- Controller: non-size error still returns HTTP 400 with the generic error region.

## Accepted risks

- **No send-time recheck.** If a user uploads attachments at 6.99MB raw total, then pastes a body text larger than ~670KB, the combined MIME message could exceed 10MB. SES will reject it and the message will be lost as today. We accept this because mass-email bodies in this app are short text; the failure mode is unchanged from today's behavior for that edge case.
- **NULL `size_bytes` treated as `MAX_FILE_SIZE`.** Existing in-flight drafts may temporarily be more restrictive than necessary. The window is short (a few days at most after deploy) and the worst case is that the user must remove and re-upload a file. No data loss.

## Out of scope (explicit)

- Showing a running total or progress bar in the composer UI.
- Per-attachment file-size display in the attachment list.
- A startup or one-shot backfill job for existing rows' `size_bytes`.
- Changes to `SendEmailService` or `MessageSendingController`.

## Files touched

- `service/src/main/resources/db/migration/V024__add_size_to_attachments.sql` *(new)*
- `service/src/main/kotlin/fi/espoo/vekkuli/config/AwsConfig.kt` — new constant
- `service/src/main/kotlin/fi/espoo/vekkuli/domain/QueuedMessage.kt` — `Attachment.sizeBytes`
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentRepository.kt` — row mapper, signatures
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentService.kt` — new param, combined check, persist size, new exception type (in same package)
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentController.kt` — new request param, new catch branch
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/employeeReservationList/components/AttachmentView.kt` — `hx-include`, error regions, new error renderer
- Corresponding unit / integration tests
