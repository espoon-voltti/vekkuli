package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class PaytrailCacheService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun putPayment(
        key: String,
        response: PaytrailPaymentResponse
    ) {
        val json = RedisMapper.serializeResponse(response)
        val gracePeriodInSeconds = 5 * 60
        val timeoutInSeconds = BoatSpaceConfig.SESSION_TIME_IN_SECONDS.toLong() + gracePeriodInSeconds
        redisTemplate.opsForValue().set(
            key,
            json,
            timeoutInSeconds,
            TimeUnit.SECONDS
        )
    }

    fun getPayment(key: String): PaytrailPaymentResponse? {
        val json = redisTemplate.opsForValue().get(key) ?: return null
        return RedisMapper.deserializeResponse(json)
    }
}

object RedisMapper {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = false
        }

    fun serializeResponse(response: PaytrailPaymentResponse): String = json.encodeToString(response)

    fun deserializeResponse(jsonString: String): PaytrailPaymentResponse = json.decodeFromString(jsonString)
}
