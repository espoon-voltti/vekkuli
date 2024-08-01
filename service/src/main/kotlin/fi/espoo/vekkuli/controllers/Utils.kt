package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.domain.getCitizen
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked

class Utils {
    companion object {
        private fun getBaseUrl(): String {
            val env = System.getenv("VOLTTI_ENV")
            val runningInDocker = System.getenv("E2E_ENV") == "docker"
            return when (env) {
                "production" -> "https://varaukset.espoo.fi"
                "staging" -> "https://staging.varaukset.espoo.fi"
                else -> {
                    if (runningInDocker) {
                        "http://api-gateway:3000"
                    } else {
                        "http://localhost:3000"
                    }
                }
            }
        }

        fun redirectUrl(path: String): String {
            val baseUrl = getBaseUrl()
            return "redirect:$baseUrl$path"
        }

        fun getCitizen(
            request: HttpServletRequest,
            jdbi: Jdbi
        ): Citizen? {
            val authenticatedUser = request.getAuthenticatedUser()
            val citizen = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
            return citizen
        }
    }
}
