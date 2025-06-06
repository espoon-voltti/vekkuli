// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import fi.espoo.vekkuli.common.Unauthorized
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*

data class AuthenticatedUser(
    val id: UUID,
    val type: String
) {
    companion object {
        val systemUserId = UUID.fromString("00000000-0000-0000-0000-000000000000")

        fun createSystemUser() = AuthenticatedUser(systemUserId, "user")
    }

    fun isSystemUser() = id == systemUserId

    fun isCitizen() = type == "citizen"

    fun isEmployee() = type == "user"
}

class JwtToAuthenticatedUser : HttpFilter() {
    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val type =
            request
                .getDecodedJwt()
                ?.getClaim("type")
                .toString()
                .trim('"')

        val user =
            request.getDecodedJwt()?.subject?.let { subject ->
                AuthenticatedUser(id = UUID.fromString(subject), type = type)
            }
        if (user != null) {
            request.setAttribute(ATTR_USER, user)
        }
        chain.doFilter(request, response)
    }
}

class HttpAccessControl : HttpFilter() {
    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        if (request.requiresAuthentication()) {
            val authenticatedUser =
                request.getAuthenticatedUser()
                    ?: return response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "fi.espoo.vekkuli.common.Unauthorized")

            when {
                request.requestURI.startsWith("/virkailija/") -> {
                    if (!authenticatedUser.isEmployee()) {
                        return response.sendError(HttpServletResponse.SC_FORBIDDEN, "fi.espoo.vekkuli.common.Forbidden")
                    }
                }
                request.requiresAuthenticatedCitizen() -> {
                    if (!authenticatedUser.isCitizen()) {
                        return response.sendError(HttpServletResponse.SC_FORBIDDEN, "fi.espoo.vekkuli.common.Forbidden")
                    }
                }
                request.requestURI.startsWith("/system/") -> {
                    if (!authenticatedUser.isSystemUser()) {
                        return response.sendError(HttpServletResponse.SC_FORBIDDEN, "fi.espoo.vekkuli.common.Forbidden")
                    }
                }
            }
        }

        chain.doFilter(request, response)
    }

    private val unautheticatedRoutes =
        setOf(
            "/",
            "/virkailija",
            "/virkailija/static",
            "/kuntalainen/venepaikat",
            "/kuntalainen/partial/vapaat-paikat",
            "/health",
            "/actuator/health",
        )

    private fun HttpServletRequest.requiresAuthentication(): Boolean =
        when {
            unautheticatedRoutes.contains(requestURI) ||
                requestURI.startsWith("/virkailija/static") ||
                requestURI.startsWith("/api/citizen/public") ||
                requestURI.startsWith("/ext") -> false
            else -> true
        }

    private fun HttpServletRequest.requiresAuthenticatedCitizen(): Boolean =
        when {
            requestURI.startsWith("/api/citizen/public") -> false
            requestURI.startsWith("/api/citizen/") ||
                requestURI.startsWith("/kuntalainen/") -> true
            else -> false
        }
}

class JwtTokenDecoder(
    private val jwtVerifier: JWTVerifier
) : HttpFilter() {
    private val logger = KotlinLogging.logger {}

    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        try {
            request
                .getBearerToken()
                ?.takeIf { it.isNotEmpty() }
                ?.let { request.setDecodedJwt(jwtVerifier.verify(it)) }
        } catch (e: JWTVerificationException) {
            logger.error(e) { "JWT token verification failed" }
        }
        chain.doFilter(request, response)
    }
}

fun HttpServletRequest.getAuthenticatedUser(): AuthenticatedUser? = getAttribute(ATTR_USER) as AuthenticatedUser?

fun HttpServletRequest.ensureCitizenId(): UUID {
    val citizen = getAttribute(ATTR_USER) as AuthenticatedUser?
    if (citizen == null || !citizen.isCitizen()) {
        throw Unauthorized("No authenticated user")
    }
    return citizen.id
}

fun HttpServletRequest.ensureEmployeeId(): UUID {
    val employee = getAttribute(ATTR_USER) as AuthenticatedUser?
    if (employee == null || !employee.isEmployee()) {
        throw Unauthorized("No authenticated user")
    }
    return employee.id
}

fun HttpServletRequest.getAuthenticatedCitizen(): AuthenticatedUser {
    val citizen = getAttribute(ATTR_USER) as AuthenticatedUser?
    if (citizen == null || !citizen.isCitizen()) {
        throw Unauthorized("No authenticated citizen")
    }
    return citizen
}

fun HttpServletRequest.getAuthenticatedEmployee(): AuthenticatedUser {
    val employee = getAttribute(ATTR_USER) as AuthenticatedUser?
    if (employee == null || !employee.isEmployee()) {
        throw Unauthorized("No authenticated employee")
    }
    return employee
}

private const val ATTR_USER = "vekkuli.user"
private const val ATTR_JWT = "vekkuli.jwt"

private fun HttpServletRequest.getDecodedJwt(): DecodedJWT? = getAttribute(ATTR_JWT) as DecodedJWT?

private fun HttpServletRequest.setDecodedJwt(jwt: DecodedJWT) = setAttribute(ATTR_JWT, jwt)

private fun HttpServletRequest.getBearerToken(): String? = getHeader("Authorization")?.substringAfter("Bearer ", missingDelimiterValue = "")
