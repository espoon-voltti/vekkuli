# SES 10MB Combined-Attachment Limit — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prevent the mass-email composer from producing drafts whose combined attachment size would cause AWS SES to refuse the message at send time.

**Architecture:** Add a raw-bytes combined cap (7 MB, leaving MIME headroom under SES's 10 MB) enforced when an attachment is uploaded. The existing HTMX upload form is taught to post the list of already-attached IDs together with each new file, so the server can sum stored attachment sizes and reject the upload before storing. A new nullable `size_bytes` column on `attachment` carries the stored sizes; existing NULL rows are treated conservatively.

**Tech Stack:** Kotlin · Spring Boot · JDBI (Postgres) · Flyway migrations · HTMX · JUnit 5 + mockito-kotlin

**Reference spec:** `docs/superpowers/specs/2026-05-22-ses-10mb-combined-attachment-limit-design.md`

---

## File Structure

**Modify:**
- `service/src/main/kotlin/fi/espoo/vekkuli/config/AwsConfig.kt` — add `MAX_RAW_ATTACHMENT_TOTAL_BYTES` constant.
- `service/src/main/kotlin/fi/espoo/vekkuli/domain/QueuedMessage.kt` — add `sizeBytes: Long?` to `Attachment`.
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentRepository.kt` — write size on insert; new `findSizesByIds`; updated row mapper SELECT.
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentService.kt` — new `existingAttachmentIds` param, combined-size check, new exception type (declared in same file's package).
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentController.kt` — accept existing IDs request param; catch `MessageSizeLimitExceededException` → HTTP 422.
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentView.kt` — `hx-include` for existing IDs, dedicated error region, `renderSizeLimitError` fragment. (The file path is `emailAttachments/AttachmentView.kt` but the declared package is `fi.espoo.vekkuli.boatSpace.employeeReservationList.components` — this is intentional; don't move the file.)

**Create:**
- `service/src/main/resources/db/migration/V024__add_size_to_attachments.sql` — Flyway migration adding `size_bytes` column.
- `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/MessageSizeLimitExceededException.kt` — new typed exception.
- `service/src/test/kotlin/fi/espoo/vekkuli/tests/AttachmentServiceTest.kt` — unit tests for combined size check.

**Don't touch:**
- `service/src/main/kotlin/fi/espoo/vekkuli/service/SendEmailService.kt` — no send-time recheck per spec.
- The mass-email send controller — its payload is unchanged.

---

## Task 1: DB migration — add `size_bytes` column

**Files:**
- Create: `service/src/main/resources/db/migration/V024__add_size_to_attachments.sql`

- [ ] **Step 1: Verify V024 is the next free Flyway version**

Run: `ls service/src/main/resources/db/migration | tail -3`
Expected: shows `V023__booking_period_id.sql` as the most recent. If a newer V024+ already exists (e.g. master moved on), bump to the next free number throughout this plan.

- [ ] **Step 2: Write the migration**

Create `service/src/main/resources/db/migration/V024__add_size_to_attachments.sql` with exactly:

```sql
ALTER TABLE attachment
    ADD COLUMN size_bytes BIGINT;
```

Rationale: nullable so existing rows stay unchanged; the service treats NULL conservatively, so no backfill job is needed.

- [ ] **Step 3: Run the Flyway migration locally to confirm it applies**

Run: `cd service && ./gradlew flywayMigrate`
Expected: build success; output mentions `V024__add_size_to_attachments` applied.

- [ ] **Step 4: Commit**

```bash
git add service/src/main/resources/db/migration/V024__add_size_to_attachments.sql
git commit -m "Add size_bytes column to attachment table"
```

---

## Task 2: Domain — add `sizeBytes` to `Attachment`

**Files:**
- Modify: `service/src/main/kotlin/fi/espoo/vekkuli/domain/QueuedMessage.kt`

- [ ] **Step 1: Add the field**

In `QueuedMessage.kt`, change the `Attachment` data class to:

```kotlin
data class Attachment(
    val key: String,
    val id: UUID,
    val name: String,
    val sizeBytes: Long? = null,
)
```

The default of `null` keeps current callers compiling; the repository (next task) populates the value when reading.

- [ ] **Step 2: Build to confirm nothing breaks**

Run: `cd service && ./gradlew compileKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add service/src/main/kotlin/fi/espoo/vekkuli/domain/QueuedMessage.kt
git commit -m "Add sizeBytes field to Attachment domain class"
```

---

## Task 3: Repository — persist size, expose `findSizesByIds`

**Files:**
- Modify: `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentRepository.kt`

- [ ] **Step 1: Update `addAttachment` signature and INSERT**

Replace the body of `addAttachment` with:

```kotlin
fun addAttachment(
    key: String,
    name: String,
    sizeBytes: Long,
): UUID {
    val id =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    "INSERT INTO attachment (key, name, size_bytes) VALUES (:key, :name, :sizeBytes)"
                )
                .bind("key", key)
                .bind("name", name)
                .bind("sizeBytes", sizeBytes)
                .executeAndReturnGeneratedKeys("id")
                .mapTo<UUID>()
                .one()
        }
    return id
}
```

- [ ] **Step 2: Update `getAttachment` SELECT to include `size_bytes`**

Replace the body of `getAttachment` with:

```kotlin
fun getAttachment(id: UUID): Attachment? =
    jdbi.withHandleUnchecked { handle ->
        handle
            .createQuery(
                "SELECT id, key, name, size_bytes AS sizeBytes FROM attachment WHERE id = :id"
            ).bind("id", id)
            .mapTo<Attachment>()
            .singleOrNull()
    }
```

(JDBI maps the aliased column `sizeBytes` to the `sizeBytes` Kotlin property; for NULL the property is `null`.)

- [ ] **Step 3: Add `findSizesByIds`**

Append inside the `AttachmentRepository` class:

```kotlin
fun findSizesByIds(ids: List<UUID>): Map<UUID, Long?> {
    if (ids.isEmpty()) return emptyMap()
    return jdbi.withHandleUnchecked { handle ->
        handle
            .createQuery(
                "SELECT id, size_bytes FROM attachment WHERE id IN (<ids>)"
            )
            .bindList("ids", ids)
            .map { rs, _ ->
                val uuid = rs.getObject("id", UUID::class.java)
                val raw = rs.getLong("size_bytes")
                val size: Long? = if (rs.wasNull()) null else raw
                uuid to size
            }
            .list()
            .toMap()
    }
}
```

- [ ] **Step 4: Update the call site in `AttachmentService.uploadAttachment`** (compile-only change; logic comes in Task 5)

In `AttachmentService.kt`, find the existing call:

```kotlin
return attachmentRepository.addAttachment(
    key,
    name
)
```

Replace with:

```kotlin
return attachmentRepository.addAttachment(
    key,
    name,
    sizeBytes = size,
)
```

- [ ] **Step 5: Build**

Run: `cd service && ./gradlew compileKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentRepository.kt service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentService.kt
git commit -m "Persist attachment size; add findSizesByIds repository method"
```

---

## Task 4: Constants — add combined-size limit

**Files:**
- Modify: `service/src/main/kotlin/fi/espoo/vekkuli/config/AwsConfig.kt`

- [ ] **Step 1: Add the new constant**

Replace the existing `AwsConstants` object body with:

```kotlin
object AwsConstants {
    const val MAX_FILE_SIZE: Long = 10L * 1000 * 1000

    /**
     * Raw (pre-MIME-encoded) combined-size cap for all attachments on a single
     * email. Chosen so the base64-encoded MIME message stays safely under the
     * AWS SES 10 MB limit, with headroom for body, headers and MIME boundaries.
     */
    const val MAX_RAW_ATTACHMENT_TOTAL_BYTES: Long = 7L * 1000 * 1000
}
```

(`MAX_FILE_SIZE` becomes `Long` so service-layer math stays consistent — the JVM-`Int` overflow at ~2 GB isn't an issue today, but the comparison sites in Task 5 do `Long` arithmetic.)

- [ ] **Step 2: Build to confirm the `Long` change doesn't break callers**

Run: `cd service && ./gradlew compileKotlin`
Expected: BUILD SUCCESSFUL. If a caller uses `MAX_FILE_SIZE` as `Int`, the compile error will point you at it; update the call site to compare against `Long` (any `Long > Long` comparison is what we want anyway).

- [ ] **Step 3: Commit**

```bash
git add service/src/main/kotlin/fi/espoo/vekkuli/config/AwsConfig.kt
git commit -m "Add MAX_RAW_ATTACHMENT_TOTAL_BYTES constant"
```

---

## Task 5: Service — combined-size check (TDD)

**Files:**
- Create: `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/MessageSizeLimitExceededException.kt`
- Modify: `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentService.kt`
- Create: `service/src/test/kotlin/fi/espoo/vekkuli/tests/AttachmentServiceTest.kt`

- [ ] **Step 1: Write the failing test file**

Create `service/src/test/kotlin/fi/espoo/vekkuli/tests/AttachmentServiceTest.kt`:

```kotlin
package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentRepository
import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.boatSpace.emailAttachments.MessageSizeLimitExceededException
import fi.espoo.vekkuli.config.AwsConstants
import fi.espoo.vekkuli.config.EmailEnv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.util.UUID

class AttachmentServiceTest {
    private lateinit var s3: S3Client
    private lateinit var repo: AttachmentRepository
    private lateinit var emailEnv: EmailEnv
    private lateinit var service: AttachmentService

    private val pdf = "application/pdf"
    private val limit = AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES
    private val perFileMax = AwsConstants.MAX_FILE_SIZE

    @BeforeEach
    fun setUp() {
        s3 = mock()
        repo = mock()
        emailEnv = mock()
        whenever(emailEnv.s3BucketName).thenReturn("test-bucket")
        whenever(repo.addAttachment(any(), any(), any())).thenReturn(UUID.randomUUID())
        service = AttachmentService(s3, repo, emailEnv)
    }

    private fun upload(size: Long, existing: List<UUID> = emptyList()) =
        service.uploadAttachment(
            contentType = pdf,
            input = ByteArrayInputStream(ByteArray(0)),
            size = size,
            name = "x.pdf",
            existingAttachmentIds = existing,
        )

    @Test
    fun `first upload at limit succeeds`() {
        val id = upload(size = limit)
        assertNotNull(id)
        verify(repo).addAttachment(any(), eq("x.pdf"), eq(limit))
    }

    @Test
    fun `first upload just over limit is rejected and S3 is not touched`() {
        assertThrows(MessageSizeLimitExceededException::class.java) {
            upload(size = limit + 1)
        }
        verify(s3, never()).putObject(any<PutObjectRequest>(), any<software.amazon.awssdk.core.sync.RequestBody>())
        verify(repo, never()).addAttachment(any(), any(), any())
    }

    @Test
    fun `combined size just over limit is rejected`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to (limit - 100L)))
        assertThrows(MessageSizeLimitExceededException::class.java) {
            upload(size = 101L, existing = listOf(existingId))
        }
    }

    @Test
    fun `combined size at limit succeeds`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to (limit - 100L)))
        val id = upload(size = 100L, existing = listOf(existingId))
        assertNotNull(id)
    }

    @Test
    fun `per-file limit still wins over combined limit`() {
        val ex =
            assertThrows(IllegalArgumentException::class.java) {
                upload(size = perFileMax + 1)
            }
        assertEquals("File size exceeds maximum allowed size", ex.message)
    }

    @Test
    fun `existing row with NULL size is treated as MAX_FILE_SIZE`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to null))
        // perFileMax is already > limit; any non-zero new upload should push over
        assertThrows(MessageSizeLimitExceededException::class.java) {
            upload(size = 1L, existing = listOf(existingId))
        }
    }

    @Test
    fun `unknown existing id contributes zero`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(emptyMap())
        val id = upload(size = 1L, existing = listOf(existingId))
        assertNotNull(id)
    }

    @Test
    fun `exception carries current attempted and limit bytes`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to (limit - 100L)))
        val ex =
            assertThrows(MessageSizeLimitExceededException::class.java) {
                upload(size = 200L, existing = listOf(existingId))
            }
        assertEquals(limit - 100L, ex.currentBytes)
        assertEquals(200L, ex.attemptedBytes)
        assertEquals(limit, ex.limitBytes)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails (compile error)**

Run: `cd service && ./gradlew test --tests "fi.espoo.vekkuli.tests.AttachmentServiceTest"`
Expected: FAIL — compilation error referencing `MessageSizeLimitExceededException`, the new `existingAttachmentIds` parameter, and `findSizesByIds`. This proves the test catches the missing pieces.

- [ ] **Step 3: Create the exception type**

Create `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/MessageSizeLimitExceededException.kt`:

```kotlin
package fi.espoo.vekkuli.boatSpace.emailAttachments

class MessageSizeLimitExceededException(
    val currentBytes: Long,
    val attemptedBytes: Long,
    val limitBytes: Long,
) : RuntimeException("Attachment combined size limit exceeded")
```

- [ ] **Step 4: Update `AttachmentService.uploadAttachment`**

Open `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentService.kt`. Replace the entire `uploadAttachment` method with:

```kotlin
fun uploadAttachment(
    contentType: String?,
    input: InputStream,
    size: Long,
    name: String,
    existingAttachmentIds: List<UUID> = emptyList(),
): UUID {
    val key = "attachment-${UUID.randomUUID()}"
    if (contentType == null) throw IllegalArgumentException("Content type must not be null")

    if (!allowedContentTypes.contains(contentType)) {
        throw IllegalArgumentException("Content type must be one of: $allowedContentTypes")
    }
    if (size < 0) throw IllegalArgumentException("Size must not be negative")
    if (size > AwsConstants.MAX_FILE_SIZE) throw IllegalArgumentException("File size exceeds maximum allowed size")

    val existingTotal =
        if (existingAttachmentIds.isEmpty()) {
            0L
        } else {
            attachmentRepository
                .findSizesByIds(existingAttachmentIds)
                .values
                .sumOf { it ?: AwsConstants.MAX_FILE_SIZE }
        }
    val projectedTotal = existingTotal + size
    if (projectedTotal > AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES) {
        throw MessageSizeLimitExceededException(
            currentBytes = existingTotal,
            attemptedBytes = size,
            limitBytes = AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES,
        )
    }

    storeAttachmentToS3(
        key = key,
        contentType = contentType,
        inputStream = input,
        size = size,
    )
    return attachmentRepository.addAttachment(
        key,
        name,
        sizeBytes = size,
    )
}
```

The `default = emptyList()` on `existingAttachmentIds` keeps any existing call sites that haven't been updated (controller comes in Task 6) compiling. Existing callers behave exactly as before (no combined check until they pass IDs).

- [ ] **Step 5: Run the test to verify it passes**

Run: `cd service && ./gradlew test --tests "fi.espoo.vekkuli.tests.AttachmentServiceTest"`
Expected: PASS — all 8 tests green.

- [ ] **Step 6: Commit**

```bash
git add service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/MessageSizeLimitExceededException.kt service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentService.kt service/src/test/kotlin/fi/espoo/vekkuli/tests/AttachmentServiceTest.kt
git commit -m "Enforce combined attachment size limit in AttachmentService"
```

---

## Task 6: Controller — accept existing IDs, map exception → HTTP 422

**Files:**
- Modify: `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentController.kt`

- [ ] **Step 1: Add the new request param and exception branch**

Replace the entire `addAttachment` method with:

```kotlin
@PostMapping("/lisaa-liite", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
@ResponseBody
fun addAttachment(
    request: HttpServletRequest,
    @RequestParam spaceId: List<Int> = emptyList(),
    @RequestParam("attachmentId") existingAttachmentIds: List<UUID> = emptyList(),
    @RequestParam file: MultipartFile?,
): ResponseEntity<String> {
    val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()

    authenticatedUser.let {
        logger.audit(
            it,
            "ADD_ATTACHMENT_STUB",
            mapOf(
                "reservationIds" to (spaceId.joinToString(", "))
            )
        )
    }
    if (!authenticatedUser.isEmployee()) {
        throw Unauthorized()
    }

    try {
        if (file != null) {
            val name = file.originalFilename ?: "unknown"
            val id =
                attachmentService
                    .uploadAttachment(
                        file.contentType,
                        file.inputStream,
                        file.size,
                        name,
                        existingAttachmentIds,
                    )
            return ResponseEntity.ok(
                attachmentView.renderAttachmentListItemWithDelete(
                    id,
                    name
                )
            )
        } else {
            return ResponseEntity.noContent().build()
        }
    } catch (e: MessageSizeLimitExceededException) {
        logger.info { "Attachment upload rejected: combined size ${e.currentBytes + e.attemptedBytes} > limit ${e.limitBytes}" }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(attachmentView.renderSizeLimitError(e.currentBytes, e.attemptedBytes, e.limitBytes))
    } catch (e: Exception) {
        logger.error(e) { "Error uploading an attachment" }
        return ResponseEntity.badRequest().build()
    }
}
```

- [ ] **Step 2: Add the new imports at the top of the file**

Append to the existing imports in `AttachmentController.kt`:

```kotlin
import fi.espoo.vekkuli.boatSpace.emailAttachments.MessageSizeLimitExceededException
import org.springframework.http.HttpStatus
```

(`AttachmentController` already lives in the same package as the exception, so the import is for clarity — Kotlin won't require it. Add it anyway to keep the file self-documenting.)

- [ ] **Step 3: Build**

Run: `cd service && ./gradlew compileKotlin`
Expected: BUILD SUCCESSFUL. The `renderSizeLimitError` method does not exist yet — leave the failing compile until Task 7 if you commit in batches. Otherwise, do Step 4 first.

- [ ] **Step 4: Commit (alongside Task 7)**

Hold this commit until Task 7's view changes are in place, so the build is green at every commit boundary. After Task 7 completes, commit both together:

```bash
git add service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentController.kt
# Continue to Task 7 before committing.
```

---

## Task 7: View — `hx-include`, error region, error renderer

**Files:**
- Modify: `service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentView.kt` (declared package: `fi.espoo.vekkuli.boatSpace.employeeReservationList.components`)

- [ ] **Step 1: Update `render()` to include existing IDs and add a dedicated error region**

Replace the entire `render()` method with:

```kotlin
//language=HTML
fun render() =
    //language=HTML
    """
    <form
      hx-post="/virkailija/viestit/lisaa-liite"
      hx-encoding="multipart/form-data"
      hx-target="#attachment-list"
      hx-swap="beforeend"
      hx-trigger="change from:#attachment-input"
      hx-include="#attachment-list input[name='attachmentId']"
      @htmx:after-request="
        const status = event.detail.xhr ? event.detail.xhr.status : 0;
        const sizeBox = document.getElementById('attachment-size-error');
        const genericBox = document.getElementById('error-box');
        sizeBox.hidden = true;
        sizeBox.innerHTML = '';
        if (event.detail.successful) {
          genericBox.hidden = true;
        } else if (status === 422) {
          genericBox.hidden = true;
          sizeBox.innerHTML = event.detail.xhr.responseText;
          sizeBox.hidden = false;
        } else {
          genericBox.hidden = false;
        }
        document.getElementById('attachment-input').value = null;
      "
      hx-indicator="#upload-indicator"
    >
      <input
        id="attachment-input"
        type="file"
        name="file"
        accept="image/png, image/jpeg, image/jpg, application/pdf"
      >
      <div id="attachment-size-error" hidden class="is-centered is-vcentered is-error-text"></div>
      <div id="error-box" hidden class="is-centered is-vcentered is-error-text">Liitteen lisäämisessä tapahtui virhe.</div>
    </form>
    <ul id="attachment-list">
      <div id="upload-indicator" class="htmx-indicator is-centered is-vcentered"> ${icons.spinner} </div>
    </ul>
    """.trimIndent()
```

Notes:
- `hx-include="#attachment-list input[name='attachmentId']"` collects every hidden `attachmentId` input rendered by `renderAttachmentListItem` and posts them as `attachmentId[]`. This matches the request param added in Task 6.
- The 422-response branch in the `@htmx:after-request` handler injects the server-rendered fragment into a dedicated `#attachment-size-error` div, leaving the generic `#error-box` for other 4xx/5xx errors.
- Keep the existing `${icons.spinner}` reference unchanged — Kotlin's string-template evaluation inserts the SVG at render time.

- [ ] **Step 2: Add `renderSizeLimitError` method**

Append inside the `AttachmentView` class (after `renderAttachmentListItem`):

```kotlin
//language=HTML
fun renderSizeLimitError(
    currentBytes: Long,
    attemptedBytes: Long,
    limitBytes: Long,
): String {
    val combinedMb = formatMb(currentBytes + attemptedBytes)
    val limitMb = formatMb(limitBytes)
    return """
        Liitteiden yhteenlaskettu koko ylittäisi sallitun rajan ($combinedMb MB / $limitMb MB).
        Poista liite tai valitse pienempi tiedosto.
    """.trimIndent()
}

private fun formatMb(bytes: Long): String {
    val mb = bytes.toDouble() / 1_000_000.0
    return String.format(java.util.Locale("fi", "FI"), "%.1f", mb)
}
```

The decimal-MB convention (`1_000_000`) matches the byte constants in `AwsConstants` (`10 * 1000 * 1000`). The Finnish locale ensures the comma decimal separator.

- [ ] **Step 3: Build the full module**

Run: `cd service && ./gradlew compileKotlin`
Expected: BUILD SUCCESSFUL — the controller from Task 6 now resolves `renderSizeLimitError`.

- [ ] **Step 4: Run the full test suite**

Run: `cd service && ./gradlew test`
Expected: BUILD SUCCESSFUL. New `AttachmentServiceTest` passes (8 tests); existing tests still pass.

- [ ] **Step 5: Commit the view + controller together**

```bash
git add service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentView.kt service/src/main/kotlin/fi/espoo/vekkuli/boatSpace/emailAttachments/AttachmentController.kt
git commit -m "Surface combined-size error in composer UI"
```

---

## Task 8: Manual smoke test

**Files:**
- None (verification only)

- [ ] **Step 1: Start the local stack**

Run: `cd compose && docker-compose up -d` (or the project's normal local-up command). Confirm Postgres, S3 mock, and the service start.

- [ ] **Step 2: Migrate**

Run: `cd service && ./gradlew flywayMigrate`
Expected: V024 applied; verify with: `psql ... -c "\d attachment"` showing `size_bytes` column.

- [ ] **Step 3: Drive the composer**

Log in as an employee, open the mass-email composer, and verify each case:

1. Attach a 3 MB PDF → uploads successfully; verify `attachment.size_bytes` row populated via `psql`.
2. Attach a second 3 MB PDF → uploads (combined 6 MB < 7 MB cap).
3. Attach a third 3 MB PDF → upload is rejected with the Finnish over-limit message in the dedicated error region; the file input is cleared; the existing attachment list is unchanged.
4. Delete one attachment, retry the third upload → succeeds.

If any case behaves unexpectedly, fix and re-test before moving on.

- [ ] **Step 4: Confirm no regression in the generic error path**

Attach a `.txt` file (disallowed MIME type). The generic `#error-box` ("Liitteen lisäämisessä tapahtui virhe.") should show, not the size error.

- [ ] **Step 5: No commit needed** — this task is verification.

---

## Self-Review Checklist (pre-merge)

- [ ] Spec coverage: every section of the design doc maps to a task above. (V024 migration → Task 1; `Attachment.sizeBytes` → Task 2; repository → Task 3; constants → Task 4; service + exception + tests → Task 5; controller → Task 6; view → Task 7; manual smoke → Task 8.)
- [ ] No `TODO` / `TBD` / "appropriate" placeholders remain in the diff.
- [ ] Every commit boundary leaves `./gradlew test` green (note: Task 6 deliberately defers its commit to Task 7 to preserve this).
- [ ] No changes to `SendEmailService` or the mass-email send controller (spec non-goal).
- [ ] No backfill job for existing rows (spec non-goal — NULL handling covers it).
