package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.domain.CitizenWithDetails
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import java.util.*

interface CitizenAccessControl {
    fun requireCitizen(): CitizenWithDetails

    fun requireCitizenId(citizenId: UUID)
}

@Component
class ContextCitizenAccessControl(
    private val citizenContextProvider: CitizenContextProvider,
) : CitizenAccessControl {
    override fun requireCitizen(): CitizenWithDetails {
        return citizenContextProvider.getCurrentCitizen() ?: throw Unauthorized()
    }

    override fun requireCitizenId(citizenId: UUID) {
        val citizen = citizenContextProvider.getCurrentCitizen()
        if (citizen?.id != citizenId) {
            throw Forbidden()
        }
    }
}

interface CitizenContextProvider {
    fun getCurrentCitizen(): CitizenWithDetails?
}

@Component
class RequestCitizenContextProvider(
    private val request: HttpServletRequest,
    private val reserverService: ReserverService,
) : CitizenContextProvider {
    override fun getCurrentCitizen(): CitizenWithDetails? {
        return getCitizen(request, reserverService)
    }
}
