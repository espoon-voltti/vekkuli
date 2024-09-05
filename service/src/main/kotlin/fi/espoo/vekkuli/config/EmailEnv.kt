package fi.espoo.vekkuli.config

import org.springframework.core.env.Environment
import software.amazon.awssdk.regions.Region

data class EmailEnv(
    val enabled: Boolean,
    val region: Region,
    val senderArn: String,
    val senderAddress: String,
) {
    companion object {
        fun fromEnvironment(env: Environment) =
            EmailEnv(
                enabled =
                    env.getProperty(
                        "EMAIL_ENABLED",
                        Boolean::class.java
                    ) ?: false,
                region =
                    env.getProperty(
                        "EMAIL_AWS_REGION",
                        Region::class.java,
                    ) ?: Region.EU_WEST_1,
                senderArn =
                    env.getProperty(
                        "EMAIL_SENDER_ARN",
                    ) ?: "",
                senderAddress =
                    env.getProperty(
                        "EMAIL_SENDER_ADDRESS",
                    ) ?: "no-reply@espoo.fi"
            )
    }
}
