package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.service.ReserverService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

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
                EnvType.LocalDocker -> "http://frontend"
                EnvType.Local -> System.getenv("BASE_URL") ?: "http://localhost:9000"
            }

        fun getEnv(): EnvType {
            val env = System.getenv("ENVIRONMENT")
            return when (env) {
                "production" -> EnvType.Production
                "staging" -> EnvType.Staging
                "local" -> EnvType.Local
                "local-docker" -> EnvType.LocalDocker
                else -> EnvType.Local
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

        fun redirectUrlThymeleaf(path: String): String = "redirect:${getServiceUrl(path)}"

        fun redirectUrl(url: String): ResponseEntity<String> =
            ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", url)
                .body("")

        fun badRequest(body: String): ResponseEntity<String> = ResponseEntity.badRequest().body(body)

        fun getCitizen(
            request: HttpServletRequest,
            service: ReserverService,
        ): CitizenWithDetails? {
            val authenticatedUser = request.getAuthenticatedUser()
            if (authenticatedUser?.isEmployee() == true) {
                return null
            }
            val citizen = authenticatedUser?.let { service.getCitizen(it.id) }
            return citizen
        }

        fun isAuthenticated(
            userType: UserType,
            request: HttpServletRequest
        ) = if (userType == UserType.CITIZEN) {
            request.getAuthenticatedUser()?.type == "citizen"
        } else {
            request.getAuthenticatedUser()?.type == "user"
        }
    }
}
