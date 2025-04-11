package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.utils.TimeProvider
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Profile("!test")
@Service
class PaytrailCacheCleanupJob(
    private val paytrailCacheService: PaytrailCacheService
) {
    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTransactions() {
        paytrailCacheService.clearExpired()
    }
}

@Service
class PaytrailCacheService(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    companion object {
        const val CACHE_DURATION_IN_DAYS = 7L
    }

    fun putPayment(
        transactionId: String,
        response: PaytrailPaymentResponse
    ) {
        val json = JsonMapper.serializeResponse(response)
        val currentTime = timeProvider.getCurrentDateTime()
        val expiresAt = currentTime.plusDays(CACHE_DURATION_IN_DAYS)

        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    INSERT INTO paytrail_payment_cache (transaction_id, response, created_at, expires_at)
                    VALUES (:transaction_id, CAST(:response AS jsonb), :currentTime, :expiresAt)
                    ON CONFLICT (transaction_id) DO UPDATE SET response = EXCLUDED.response, expires_at = EXCLUDED.expires_at
                    """
                ).bind("transaction_id", transactionId)
                .bind("response", json)
                .bind("currentTime", currentTime)
                .bind("expiresAt", expiresAt)
                .execute()
        }
    }

    fun getPayment(transactionId: String): PaytrailPaymentResponse? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT response FROM paytrail_payment_cache
                    WHERE transaction_id = :transaction_id
                    """
                ).bind("transaction_id", transactionId)
                .mapTo(String::class.java)
                .findOne()
                .map(JsonMapper::deserializeResponse)
                .orElse(null)
        }

    fun clearExpired() {
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    "DELETE FROM paytrail_payment_cache WHERE expires_at < :currentTime"
                ).bind("currentTime", timeProvider.getCurrentDateTime())
                .execute()
        }
    }
}

object JsonMapper {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = false
        }

    fun serializeResponse(response: PaytrailPaymentResponse): String = json.encodeToString(response)

    fun deserializeResponse(jsonString: String): PaytrailPaymentResponse = json.decodeFromString(jsonString)
}
