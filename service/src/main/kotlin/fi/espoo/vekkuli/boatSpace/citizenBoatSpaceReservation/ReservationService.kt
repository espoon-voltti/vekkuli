package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateReservationService
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.BoatSpaceConfig.doesBoatFit
import fi.espoo.vekkuli.config.Dimensions
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@Service
class ReservationService(
    private val boatReservationService: BoatReservationService,
    private val seasonalService: SeasonalService,
    private val timeProvider: TimeProvider,
    private val reservationFormServiceAdapter: ReservationFormServiceAdapter,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val citizenAccessControl: CitizenAccessControl,
    private val reservationPaymentService: ReservationPaymentService,
    private val terminateService: TerminateReservationService,
) {
    fun getUnfinishedReservationForCurrentCitizen(): BoatSpaceReservation? {
        val (citizenId) = citizenAccessControl.requireCitizen()
        return boatReservationService.getUnfinishedReservationForCitizen(citizenId)?.toBoatSpaceReservation()
    }

    fun getActiveReservationsForCurrentCitizen(): List<BoatSpaceReservation> {
        val (citizenId) = citizenAccessControl.requireCitizen()
        return boatReservationService.getBoatSpaceReservationsForCitizen(citizenId).map { it.toBoatSpaceReservation() }
    }

    fun getExpiredReservationsForCurrentCitizen(): List<BoatSpaceReservation> {
        val (citizenId) = citizenAccessControl.requireCitizen()
        return boatReservationService.getExpiredBoatSpaceReservationsForCitizen(citizenId).map { it.toBoatSpaceReservation() }
    }

    fun getReservation(reservationId: Int): BoatSpaceReservation {
        return accessReservation(reservationId).toBoatSpaceReservation()
    }

    @Transactional
    fun startReservation(spaceId: Int): BoatSpaceReservation {
        val (citizenId) = citizenAccessControl.requireCitizen()
        val boatSpace = boatSpaceRepository.getBoatSpace(spaceId) ?: throw NotFound("Boat space not found")
        val result = seasonalService.canReserveANewSpace(citizenId, boatSpace.type)
        if (result is ReservationResult.Failure) {
            throw Forbidden("Citizen can not reserve slip", result.errorCode.toString())
        }

        if (citizenHasExistingUnfinishedReservation(citizenId)) {
            throw Forbidden("Citizen can not have multiple reservations open")
        }

        val today = timeProvider.getCurrentDate()
        return boatReservationService.insertBoatSpaceReservation(
            citizenId,
            citizenId,
            spaceId,
            today,
            getEndDate(result),
        )
    }

    @Transactional
    fun fillReservationInformation(
        reservationId: Int,
        information: ReservationInformation
    ): BoatSpaceReservation {
        val (citizenId) = citizenAccessControl.requireCitizen()
        val reservation = accessReservation(reservationId)

        if (reservation.status != ReservationStatus.Info) {
            throw Conflict("Reservation is already filled")
        }

        return reservationFormServiceAdapter.fillReservationInformation(citizenId, reservationId, information)
    }

    @Transactional
    fun cancelUnfinishedReservation(reservationId: Int) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        validateCurrentCitizenAccessToReservation(reservationId)
        return reservationFormServiceAdapter.cancelUnfinishedReservation(citizenId, reservationId)
    }

    @Transactional
    suspend fun getPaymentInformation(reservationId: Int): PaytrailPaymentResponse {
        val citizen = citizenAccessControl.requireCitizen()
        val reservation = accessReservation(reservationId)

        if (reservation.status != ReservationStatus.Payment) {
            throw Conflict("Reservation is not filled")
        }

        return reservationPaymentService.createPaymentForBoatSpaceReservation(citizen, reservation)
    }

    @Transactional
    fun terminateReservation(reservationId: Int) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        validateCurrentCitizenAccessToReservation(reservationId)
        return terminateService.terminateBoatSpaceReservationAsOwner(reservationId, citizenId)
    }

    fun validateBoatType(
        reservationId: Int,
        boatType: BoatType
    ): Boolean {
        val reservation = accessReservation(reservationId)
        return reservation.excludedBoatTypes?.contains(boatType) != true
    }

    fun validateBoatSize(
        reservationId: Int,
        width: Int,
        length: Int
    ): Boolean {
        val reservation = accessReservation(reservationId)
        val boatDimensions = Dimensions(width, length)
        val spaceDimensions = Dimensions(reservation.boatSpaceWidthCm, reservation.boatSpaceLengthCm)
        return doesBoatFit(spaceDimensions, reservation.amenity, boatDimensions)
    }

    fun validateBoatWeight(
        reservationId: Int,
        weight: Int
    ): Boolean {
        // validate access to reservation even as it is not needed for the validation logic
        validateCurrentCitizenAccessToReservation(reservationId)
        return weight <= BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG
    }

    private fun citizenHasExistingUnfinishedReservation(citizenId: UUID): Boolean {
        return boatReservationService.getUnfinishedReservationForCitizen(citizenId) != null
    }

    private fun getEndDate(result: ReservationResult): LocalDate {
        val endOfYear = LocalDate.of(timeProvider.getCurrentDate().year, Month.DECEMBER, 31)
        val endDate =
            if (result is ReservationResult.Success) {
                result.data.endDate
            } else {
                endOfYear
            }
        return endDate
    }

    private fun validateCurrentCitizenAccessToReservation(reservation: BoatSpaceReservationDetails) {
        val citizen = citizenAccessControl.requireCitizen()
        if (!citizenHasAccessToReservation(citizen.id, reservation)) {
            throw Unauthorized()
        }
    }

    private fun citizenHasAccessToReservation(
        citizenId: UUID,
        reservation: BoatSpaceReservationDetails
    ): Boolean {
        return reservation.reserverId == citizenId || reservation.actingCitizenId == citizenId
    }

    private fun validateCurrentCitizenAccessToReservation(reservationId: Int) {
        accessReservation(reservationId)
    }

    private fun accessReservation(reservationId: Int): BoatSpaceReservationDetails {
        val result = boatReservationService.getBoatSpaceReservation(reservationId) ?: throw NotFound()
        validateCurrentCitizenAccessToReservation(result)
        return result
    }
}

data class ReservationInformation(
    val citizen: Citizen,
    val organization: Organization? = null,
    val boat: Boat,
    val certifyInformation: Boolean = false,
    val agreeToRules: Boolean = false,
) {
    data class Citizen(
        val email: String,
        val phone: String,
    )

    data class Organization(
        val id: UUID? = null,
        val name: String,
        val businessId: String,
        val municipalityCode: String,
        val phone: String,
        val email: String,
        val address: String? = null,
        val postalCode: String? = null,
        val city: String? = null,
    )

    data class Boat(
        val id: Int?,
        val name: String,
        val type: BoatType,
        val width: BigDecimal,
        val length: BigDecimal,
        val depth: BigDecimal,
        val weight: Int,
        val registrationNumber: String,
        val hasNoRegistrationNumber: Boolean = false,
        val otherIdentification: String,
        val extraInformation: String? = null,
        val ownership: OwnershipStatus,
    )
}
