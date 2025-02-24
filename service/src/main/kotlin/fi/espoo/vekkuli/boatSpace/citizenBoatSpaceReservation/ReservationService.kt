package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.SwitchPolicyService
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
import fi.espoo.vekkuli.utils.SecondsRemaining
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@Service
open class ReservationService(
    private val boatReservationService: BoatReservationService,
    private val seasonalService: SeasonalService,
    private val timeProvider: TimeProvider,
    private val reservationFormServiceAdapter: ReservationFormServiceAdapter,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val citizenAccessControl: CitizenAccessControl,
    private val reservationPaymentService: ReservationPaymentService,
    private val terminateService: TerminateReservationService,
    private val organizationService: OrganizationService,
    private val paymentService: PaymentService,
    private val permissionService: PermissionService,
    private val switchPolicyService: SwitchPolicyService
) {
    fun getUnfinishedReservationForCurrentCitizen(): BoatSpaceReservation? {
        val (citizenId) = citizenAccessControl.requireCitizen()
        val reservation = boatReservationService.getUnfinishedReservationForCitizen(citizenId)?.toBoatSpaceReservation() ?: return null
        return reservation
    }

    fun canReserveANewSpaceForCurrentCitizen(spaceId: Int): Boolean {
        val (citizenId) = citizenAccessControl.requireCitizen()
        val boatSpace = boatSpaceRepository.getBoatSpace(spaceId) ?: throw NotFound("Boat space not found")
        val result = seasonalService.canReserveANewSpace(citizenId, boatSpace.type)
        return result is ReservationResult.Success
    }

    fun getUnfinishedReservationExpirationForCurrentCitizen(): SecondsRemaining? {
        val reservation = getUnfinishedReservationForCurrentCitizen() ?: return null
        return BoatSpaceConfig.getUnfinishedReservationExpirationTime(reservation.created, timeProvider.getCurrentDateTime())
    }

    fun getActiveReservationsForCurrentCitizen(): List<BoatSpaceReservationDetails> {
        val (citizenId) = citizenAccessControl.requireCitizen()
        return boatReservationService.getBoatSpaceReservationsForReserver(citizenId)
    }

    fun getExpiredReservationsForCurrentCitizen(): List<BoatSpaceReservationDetails> {
        val (citizenId) = citizenAccessControl.requireCitizen()
        return boatReservationService.getExpiredBoatSpaceReservationsForReserver(citizenId)
    }

    fun getActiveReservationsForOrganization(orgId: UUID): List<BoatSpaceReservationDetails> =
        boatReservationService.getBoatSpaceReservationsForReserver(orgId)

    fun getExpiredReservationsForOrganization(orgId: UUID): List<BoatSpaceReservationDetails> =
        boatReservationService.getExpiredBoatSpaceReservationsForReserver(orgId)

    fun getReservation(reservationId: Int): BoatSpaceReservation = accessReservationAsCurrentCitizen(reservationId).toBoatSpaceReservation()

    fun startReservation(spaceId: Int): BoatSpaceReservation {
        val (citizenId) = citizenAccessControl.requireCitizen()

        val boatSpace = boatSpaceRepository.getBoatSpace(spaceId) ?: throw NotFound("Boat space not found")
        val organizations: List<Organization> = organizationService.getCitizenOrganizations(citizenId)

        val citizenResult = seasonalService.canReserveANewSpace(citizenId, boatSpace.type)
        val reservationResults =
            organizations
                .map {
                    seasonalService.canReserveANewSpace(it.id, boatSpace.type)
                }.plus(citizenResult)

        val canReserve = reservationResults.any { it is ReservationResult.Success }

        val today = timeProvider.getCurrentDate()

        if (!canReserve) {
            throw Forbidden(
                "Citizen and their organizations can not reserve slip",
                ReservationResultErrorCode.NotPossible.toString()
            )
        }

        if (citizenHasExistingUnfinishedReservation(citizenId)) {
            throw Forbidden("Citizen can not have multiple reservations open")
        }

        val validity =
            if (citizenResult is ReservationResult.Success) {
                citizenResult.data.reservationValidity
            } else {
                ReservationValidity.FixedTerm
            }

        return boatReservationService.insertBoatSpaceReservation(
            citizenId,
            citizenId,
            spaceId,
            CreationType.New,
            today,
            getEndDate(citizenResult),
            validity
        )
    }

    fun checkReservationAvailabilityForCurrentCitizen(spaceId: Int): CanReserveResult {
        val (citizenId) = citizenAccessControl.requireCitizen()
        return checkReservationAvailability(citizenId, spaceId)
    }

    fun checkReservationAvailability(
        citizenId: UUID,
        spaceId: Int
    ): CanReserveResult {
        // Make sure the target space isn't reserved already
        if (!boatSpaceRepository.isBoatSpaceAvailable(spaceId)) {
            return CanReserveResult(
                status = CanReserveResultStatus.CanNotReserve,
                emptyList(),
                emptyList()
            )
        }
        val boatSpace = boatSpaceRepository.getBoatSpace(spaceId) ?: throw NotFound("Boat space not found")
        val reservations = boatReservationService.getBoatSpaceReservationsForReserver(citizenId)
        val organizationReservations =
            organizationService
                .getCitizenOrganizations(citizenId)
                .map { Pair(it.name, boatReservationService.getBoatSpaceReservationsForReserver(it.id)) }
        val canReserveSpaceResult = seasonalService.canReserveANewSpace(citizenId, boatSpace.type)
        val canReserveOrganizationSpaceResult =
            organizationService
                .getCitizenOrganizations(citizenId)
                .any {
                    seasonalService.canReserveANewSpace(it.id, boatSpace.type) is ReservationResult.Success
                }

        val switchableReservations =
            reservations.filter {
                switchPolicyService.citizenCanSwitchToReservation(it.id, citizenId, spaceId) is ReservationResult.Success
            }

        val switchableOrganizationReservations: List<SwitchableOrganizationReservation> =
            organizationReservations
                .map {
                    SwitchableOrganizationReservation(
                        it.first,
                        it.second.filter { reservation ->
                            switchPolicyService.citizenCanSwitchToReservation(
                                reservation.id,
                                citizenId,
                                spaceId
                            ) is ReservationResult.Success
                        }
                    )
                }.filter { it.reservations.isNotEmpty() }

        if (canReserveSpaceResult is ReservationResult.Failure) {
            if (canReserveOrganizationSpaceResult &&
                canReserveSpaceResult.errorCode == ReservationResultErrorCode.MaxReservations
            ) {
                // Can not reserve for reserver, but can reserve for organization
                return CanReserveResult(
                    status = CanReserveResultStatus.CanReserveOnlyForOrganization,
                    switchableReservations,
                    switchableOrganizationReservations
                )
            }
            // Can not reserve but might be able to switch
            return CanReserveResult(
                status = CanReserveResultStatus.CanNotReserve,
                switchableReservations,
                switchableOrganizationReservations
            )
        }
        // Can reserve and switch if previous reservations
        return CanReserveResult(
            status = CanReserveResultStatus.CanReserve,
            switchableReservations,
            switchableOrganizationReservations
        )
    }

    @Transactional
    open fun fillReservationInformation(
        reservationId: Int,
        information: ReservationInformation
    ): BoatSpaceReservation {
        val (citizenId) = citizenAccessControl.requireCitizen()
        val reservation = accessReservationAsCurrentCitizen(reservationId)

        if (reservation.status != ReservationStatus.Info) {
            throw Conflict("Reservation is already filled")
        }

        return reservationFormServiceAdapter.fillReservationInformation(citizenId, reservationId, information)
    }

    @Transactional
    open fun cancelUnfinishedReservation(reservationId: Int) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        validateCurrentCitizenAccessToReservation(reservationId)
        return reservationFormServiceAdapter.cancelUnfinishedReservation(citizenId, reservationId)
    }

    @Transactional
    open suspend fun getPaymentInformation(reservationId: Int): PaytrailPaymentResponse {
        val citizen = citizenAccessControl.requireCitizen()
        val reservation = accessReservationAsCurrentCitizen(reservationId)

        if (reservation.status != ReservationStatus.Payment) {
            throw Conflict("Reservation is not filled")
        }

        return reservationPaymentService.createPaymentForBoatSpaceReservation(citizen, reservation)
    }

    @Transactional
    open fun terminateReservation(reservationId: Int) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        validateCurrentCitizenAccessToReservation(reservationId)
        if (!permissionService.canTerminateBoatSpaceReservation(citizenId, reservationId)) throw Unauthorized()
        return terminateService.terminateBoatSpaceReservationAsOwner(reservationId, citizenId)
    }

    @Transactional
    open fun cancelUnfinishedReservationPaymentState(reservationId: Int): BoatSpaceReservation {
        citizenAccessControl.requireCitizen()
        val reservation = accessReservationAsCurrentCitizen(reservationId)
        if (reservation.status != ReservationStatus.Payment) {
            throw Conflict("Reservation is not in payment state")
        }
        val updatedReservation = boatReservationService.setReservationStatusToInfo(reservation.id)
        paymentService.deletePaymentInCreatedStatusForReservation(reservationId)

        return updatedReservation
    }

    fun validateBoatType(
        reservationId: Int,
        boatType: BoatType
    ): Boolean {
        val reservation = accessReservationAsCurrentCitizen(reservationId)
        return reservation.excludedBoatTypes?.contains(boatType) != true
    }

    fun validateBoatSize(
        reservationId: Int,
        width: Int,
        length: Int
    ): Boolean {
        val reservation = accessReservationAsCurrentCitizen(reservationId)
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

    private fun citizenHasExistingUnfinishedReservation(citizenId: UUID): Boolean =
        boatReservationService.getUnfinishedReservationForCitizen(citizenId) != null

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
    ): Boolean = permissionService.hasAccessToReservation(citizenId, reservation.id)

    private fun validateCurrentCitizenAccessToReservation(reservationId: Int) {
        accessReservationAsCurrentCitizen(reservationId)
    }

    private fun accessReservationAsCurrentCitizen(reservationId: Int): BoatSpaceReservationDetails {
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
    val storageType: StorageType? = null,
    val trailer: Trailer? = null,
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

    data class Trailer(
        val id: Int?,
        val registrationCode: String,
        val width: BigDecimal,
        val length: BigDecimal,
    )
}
