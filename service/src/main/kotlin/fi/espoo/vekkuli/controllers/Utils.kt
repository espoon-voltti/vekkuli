package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.service.CitizenService
import jakarta.servlet.http.HttpServletRequest

enum class EnvType {
    Production,
    Staging,
    Local,
    LocalDocker
}

class Utils {
    companion object {
        fun getBaseUrl(): String =
            when (getEnv()) {
                EnvType.Production -> "https://varaukset.espoo.fi"
                EnvType.Staging -> "https://staging.varaukset.espoo.fi"
                EnvType.LocalDocker -> "http://api-gateway:3000"
                EnvType.Local -> "http://localhost:3000"
            }

        fun getEnv(): EnvType {
            val env = System.getenv("VOLTTI_ENV")
            val runningInDocker = System.getenv("E2E_ENV") == "docker"
            return when (env) {
                "production" -> EnvType.Production
                "staging" -> EnvType.Staging
                else -> {
                    if (runningInDocker) {
                        EnvType.LocalDocker
                    } else {
                        EnvType.Local
                    }
                }
            }
        }

        fun isStagingOrProduction(): Boolean {
            val env = getEnv()
            return env == EnvType.Staging || env == EnvType.Production
        }

        fun getServiceUrl(path: String): String {
            val baseUrl = getBaseUrl()
            return "$baseUrl$path"
        }

        fun redirectUrl(path: String): String = "redirect:${getServiceUrl(path)}"

        fun getCitizen(
            request: HttpServletRequest,
            service: CitizenService,
        ): Citizen? {
            val authenticatedUser = request.getAuthenticatedUser()
            val citizen = authenticatedUser?.let { service.getCitizen(it.id) }
            return citizen
        }
    }
}
