package fi.espoo.vekkuli.config

import org.springframework.core.env.Environment
import software.amazon.awssdk.regions.Region

data class EmailEnv(
    val enabled: Boolean,
    val region: Region,
    val senderArn: String,
    val senderAddress: String,
    val employeeAddress: String,
) {
    companion object {
        fun fromEnvironment(env: Environment) =
            EmailEnv(
                enabled =
                    env.getProperty(
                        "vekkuli.email.enabled",
                        Boolean::class.java
                    ) ?: false,
                region =
                    env.getProperty(
                        "vekkuli.email.region",
                        Region::class.java,
                    ) ?: Region.EU_WEST_1,
                senderArn =
                    env.getProperty(
                        "vekkuli.email.sender_arn",
                    ) ?: "",
                senderAddress =
                    env.getProperty(
                        "vekkuli.email.sender_address",
                    ) ?: "no-reply@espoo.fi",
                employeeAddress =
                    env.getProperty(
                        "vekkuli.email.employee_address",
                    ) ?: "venepaikat@espoo.fi"
            )
    }
}
