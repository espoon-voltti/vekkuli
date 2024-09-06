package fi.espoo.vekkuli.config

import org.springframework.core.env.Environment

data class PaytrailEnv(
    val merchantId: String,
    val merchantSecret: String
) {
    companion object {
        fun fromEnvironment(env: Environment) =
            PaytrailEnv(
                merchantId =
                    env.getProperty(
                        "vekkuli.paytrail.merchant_id",
                    ) ?: "no merchant id",
                merchantSecret =
                    env.getProperty(
                        "vekkuli.paytrail.merchant_secret",
                    ) ?: "no merchant secret",
            )
    }
}
