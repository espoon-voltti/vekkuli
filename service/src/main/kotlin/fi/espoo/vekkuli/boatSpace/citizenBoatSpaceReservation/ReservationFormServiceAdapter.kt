package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.toBoatSpaceReservation
import fi.espoo.vekkuli.service.BoatReservationService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.stereotype.Service
import java.util.*

val logger = KotlinLogging.logger {}

@Service
class ReservationFormServiceAdapter(
    private val reservationFormService: ReservationFormService,
    private val boatReservationService: BoatReservationService,
) {
    fun fillReservationInformation(
        citizenId: UUID,
        reservationId: Int,
        information: ReservationInformation
    ): BoatSpaceReservation {
        val input = information.toReservationInput(reservationId)
        reservationFormService.createOrUpdateReserverAndReservationForCitizen(reservationId, citizenId, input)
        val reservation = boatReservationService.getBoatSpaceReservation(reservationId) ?: throw NotFound()
        return reservation.toBoatSpaceReservation()
    }

    fun cancelUnfinishedReservation(
        citizenId: UUID,
        reservationId: Int
    ) {
        reservationFormService.removeBoatSpaceReservation(reservationId, citizenId)
    }
}

fun ReservationInformation.toReservationInput(reservationId: Int): ReservationInput {
    val result =
        ReservationInput(
            reservationId = reservationId,
            boatId = boat.id,
            boatType = boat.type,
            width = boat.width,
            length = boat.length,
            depth = boat.depth,
            weight = boat.weight,
            boatName = boat.name,
            extraInformation = boat.extraInformation,
            noRegistrationNumber = boat.hasNoRegistrationNumber,
            boatRegistrationNumber = boat.registrationNumber,
            otherIdentification = boat.otherIdentification,
            ownership = boat.ownership,
            firstName = null,
            lastName = null,
            ssn = null,
            address = null,
            postalCode = null,
            postalOffice = null,
            city = null,
            municipalityCode = null,
            citizenId = null,
            email = citizen.email,
            phone = citizen.phone,
            certifyInformation = certifyInformation,
            agreeToRules = agreeToRules,
            isOrganization = organization != null,
            organizationId = organization?.id,
            orgName = organization?.name,
            orgBusinessId = organization?.businessId,
            orgMunicipalityCode = organization?.municipalityCode,
            orgPhone = organization?.phone,
            orgEmail = organization?.email,
            orgAddress = organization?.address,
            orgPostalCode = organization?.postalCode,
            orgCity = organization?.city,
            citizenSelection = null,
            storageType = storageType,
            trailerRegistrationNumber = trailer?.registrationCode,
            trailerWidth = trailer?.width,
            trailerLength = trailer?.length,
        )

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    val violations = validator.validate(result)

    if (violations.isNotEmpty()) {
        logger.error { "Validation failed for ReservationInput: $this" }
        throw ConstraintViolationException(violations)
    }

    return result
}
