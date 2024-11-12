package fi.espoo.vekkuli.config

import org.springframework.core.env.Environment

data class EspiEnv(
    val apiUrl: String,
    val apiUsername: String,
    val apiPassword: String
) {
    companion object {
        fun fromEnvironment(env: Environment) =
            EspiEnv(
                apiUrl =
                    env.getProperty(
                        "vekkuli.espi.api_url",
                    ) ?: "no api url",
                apiUsername =
                    env.getProperty(
                        "vekkuli.espi.api_username",
                    ) ?: "no api username",
                apiPassword =
                    env.getProperty(
                        "vekkuli.espi.api_password",
                    ) ?: "no api password",
            )
    }
}
