package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.renewal.RenewalReservationForApplicationForm
import fi.espoo.vekkuli.boatSpace.renewal.RenewalReservationInput
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.config.BoatSpaceConfig.isEspooCitizen
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

data class BoatSpaceSwitchViewParams(
    val reservation: RenewalReservationForApplicationForm,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: RenewalReservationInput,
    val userType: UserType,
)

@Service
class BoatSpaceSwitchService(
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val organizationService: OrganizationService,
    private val seasonalService: SeasonalService,
    private val reserverService: ReserverService,
    private val boatReservationService: BoatReservationService,
    private val boatSpaceSwitchRepository: BoatSpaceSwitchRepository,
    private val reservationService: ReservationFormService,
    private val invoiceService: BoatSpaceInvoiceService,
    private val jdbiTrailerRepository: JdbiTrailerRepository,
) {
    // ** Switchs the boat space reservation with the given reservationId to the given reserverId. */
    @Transactional
    fun switchBoatSpaceReservationAsEmployee(
        userId: UUID,
        reserverId: UUID,
        oldReservationId: Int,
        newReservationId: Int
    ) {
        val reservation =
            boatSpaceReservationRepo.getReservationWithDependencies(
                oldReservationId
            ) ?: throw IllegalArgumentException("Reservation not found")
        val reserver =
            reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        val isEspooCitizen = isEspooCitizen(reserver.municipalityCode)
        val reservationResult =
            seasonalService.canSwitchReservation(
                reservation.type,
                reservation.startDate,
                reservation.endDate,
                reservation.validity,
                isEspooCitizen
            )

        // cancel old reservation
        cancelSwitchReservation(oldReservationId, reserverId)

        // create new reservation
    }

    fun switchBoatSpaceReservationAsCitizen(
        reserverId: UUID,
        reservationId: Int,
        newReservationId: Int
    ) {
        val reservation =
            boatSpaceReservationRepo.getReservationWithDependencies(
                reservationId
            ) ?: throw IllegalArgumentException("Reservation not found")
        val reserver =
            reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        val reservationResult =
            seasonalService.canSwitchReservation(
                reservation.type,
                reservation.startDate,
                reservation.endDate,
                reservation.validity,
                isEspooCitizen(reserver.municipalityCode)
            )
        if (!reservationResult.success) {
            throw Forbidden("Cannot switch reservation")
        }
    }

    fun getOrCreateSwitchReservationForEmployee(
        userId: UUID,
        originalReservationId: Int,
        reserverMunicipalityCode: Int
    ): ReservationWithDependencies {
        val original = boatSpaceSwitchRepository.getSwitchReservationForEmployee(userId, originalReservationId)
        if (original != null) return original

        val originalReservation =
            createSwitchReservation(originalReservationId, userId, UserType.EMPLOYEE, isEspooCitizen(reserverMunicipalityCode))
                ?: throw IllegalStateException("Reservation not found")
        return originalReservation
    }

    fun getOrCreateSwitchReservationForCitizen(
        userId: UUID,
        originalReservationId: Int,
        reserverMunicipalityCode: Int
    ): ReservationWithDependencies {
        val original = boatSpaceSwitchRepository.getSwitchReservationForCitizen(userId, originalReservationId)
        if (original != null) return original

        val originalReservation =
            createSwitchReservation(originalReservationId, userId, UserType.CITIZEN, isEspooCitizen(reserverMunicipalityCode))
                ?: throw IllegalStateException("Reservation not found")
        return originalReservation
    }

    fun getSwitchReservationForCitizen(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceSwitchRepository
            .getSwitchReservationForCitizen(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun getSwitchReservationForEmployee(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceSwitchRepository.getSwitchReservationForEmployee(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun cancelSwitchReservation(
        originalReservationId: Int,
        citizenId: UUID
    ) {
        boatReservationService.removeBoatSpaceReservation(originalReservationId, citizenId)
    }

    fun updateSwitchReservation(
        citizenId: UUID,
        input: RenewalReservationInput,
        reservationId: Int
    ) {
        val reservation =
            boatReservationService.getReservationWithReserver(reservationId)
                ?: throw NotFound("Reservation not found")

        if (reservation.reserverId == null) {
            throw UnauthorizedException()
        }
        updateReserver(reservation.reserverType, reservation.reserverId, input)

        reservationService.processBoatSpaceReservation(
            reserverId = reservation.reserverId,
            reservationService.buildReserveBoatSpaceInput(reservationId, input),
            ReservationStatus.Payment,
            reservation.validity ?: ReservationValidity.FixedTerm,
            reservation.startDate,
            reservation.endDate
        )
    }

    fun updateReserver(
        reserverType: ReserverType?,
        reserverId: UUID,
        input: RenewalReservationInput,
    ) {
        if (reserverType == ReserverType.Organization) {
            organizationService.updateOrganization(
                UpdateOrganizationParams(
                    id = reserverId,
                    phone = input.orgPhone,
                    email = input.orgEmail
                )
            )
        } else {
            reserverService.updateCitizen(
                UpdateCitizenParams(
                    id = reserverId,
                    phone = input.phone,
                    email = input.email,
                )
            )
        }
    }

    fun getSendInvoiceModel(reservationId: Int): SendInvoiceModel {
        val reservation = boatReservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation or reserver not found")
        }

        val invoiceData = invoiceService.createInvoiceData(reservationId, reservation.reserverId)
        if (invoiceData == null) {
            throw IllegalArgumentException("Failed to create invoice data")
        }

        // TODO: get the actual data
        return SendInvoiceModel(
            reservationId = reservationId,
            reserverName = "${invoiceData.firstnames} ${invoiceData.lastname}",
            reserverSsn = invoiceData.ssn ?: "",
            reserverAddress = "${invoiceData.street} ${invoiceData.postalCode} ${invoiceData.post}",
            product = reservation.locationName,
            billingPeriodStart = "",
            billingPeriodEnd = "",
            boatingSeasonStart = LocalDate.of(2025, 5, 1),
            boatingSeasonEnd = LocalDate.of(2025, 9, 30),
            invoiceNumber = "",
            dueDate = LocalDate.of(2025, 12, 31),
            costCenter = "?",
            invoiceType = "?",
            priceWithTax = intToDecimal(reservation.priceCents),
            description = invoiceData.description,
            contactPerson = "",
            orgId = invoiceData.orgId ?: "",
            function = getDefaultFunction(reservation.type),
        )
    }

    fun getDefaultFunction(boatSpaceType: BoatSpaceType): String =
        when (boatSpaceType) {
            BoatSpaceType.Slip -> "T1270"
            BoatSpaceType.Winter -> "T1271"
            BoatSpaceType.Storage -> "T1276"
            BoatSpaceType.Trailer -> "T1270"
        }

    @Transactional
    fun activateSwitchAndSendInvoice(
        renewedReservationId: Int,
        reserverId: UUID?,
        originalReservationId: Int?
    ) {
        if (reserverId == null || originalReservationId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceData =
            invoiceService.createInvoiceData(renewedReservationId, reserverId)
                ?: throw InternalError("Failed to create invoice batch")

        boatReservationService.setReservationStatusToInvoiced(renewedReservationId)

        boatReservationService.markReservationEnded(originalReservationId)

        invoiceService.createAndSendInvoice(invoiceData, reserverId, renewedReservationId)
            ?: throw InternalError("Failed to send invoice")
    }

    fun createSwitchReservation(
        originalReservationId: Int,
        userId: UUID,
        userType: UserType,
        isEspooCitizen: Boolean,
    ): ReservationWithDependencies? {
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservation(originalReservationId)
                ?: throw BadRequest("Reservation to renew not found")
        if (!seasonalService
                .canSwitchReservation(
                    reservation.type,
                    reservation.startDate,
                    reservation.endDate,
                    reservation.validity,
                    isEspooCitizen
                ).success
        ) {
            throw Conflict("Reservation cannot be renewed")
        }
        val newId = boatSpaceSwitchRepository.createSwitchRow(originalReservationId, userType, userId)
        return boatSpaceReservationRepo.getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(newId)
    }

    /**

     fun buildBoatSpaceSwitchViewParams(
     citizenId: UUID,
     renewedReservation: ReservationWithDependencies,
     formInput: RenewalReservationInput,
     ): BoatSpaceSwitchViewParams {
     val citizen = reserverService.getCitizen(citizenId)
     if (citizen == null || renewedReservation.reserverId != citizenId) {
     throw UnauthorizedException()
     }

     var input =
     formInput.copy(
     email = formInput.email ?: citizen?.email,
     phone = formInput.phone ?: citizen?.phone,
     storageType =
     renewedReservation.storageType ?: StorageType.None,
     )
     if (renewedReservation.trailerId != null) {
     val trailer = jdbiTrailerRepository.getTrailer(renewedReservation.trailerId) ?: throw BadRequest("Trailer not found")
     input =
     input.copy(
     trailerLength = intToDecimal(trailer?.lengthCm),
     trailerWidth = intToDecimal(trailer?.widthCm),
     trailerRegistrationNumber = trailer?.registrationCode,
     )
     }

     val usedBoatId = formInput.boatId ?: renewedReservation.boatId // use boat id from reservation if it exists
     if (usedBoatId != null && usedBoatId != 0) {
     val boat = boatService.getBoat(usedBoatId)

     if (boat != null) {
     input =
     input.copy(
     boatId = boat.id,
     depth = intToDecimal(boat.depthCm),
     boatName = boat.name,
     weight = boat.weightKg,
     width = intToDecimal(boat.widthCm),
     length = intToDecimal(boat.lengthCm),
     otherIdentification = boat.otherIdentification,
     extraInformation = boat.extraInformation,
     ownership = boat.ownership,
     boatType = boat.type,
     boatRegistrationNumber = boat.registrationCode,
     noRegistrationNumber = boat.registrationCode.isNullOrEmpty()
     )
     }
     } else {
     input = input.copy(boatId = 0)
     }

     val reserverId = renewedReservation.reserverId

     val boats =
     reserverId?.let {
     boatService
     .getBoatsForReserver(reserverId)
     .map { boat -> boat.updateBoatDisplayName(messageUtil) }
     } ?: emptyList()

     val renewedReservationForApplicationForm = buildReservationForApplicationForm(renewedReservation)
     return BoatSpaceSwitchViewParams(
     renewedReservationForApplicationForm,
     boats,
     citizen,
     input,
     UserType.CITIZEN,
     )
     }

     private fun buildReservationForApplicationForm(reservationWithDependencies: ReservationWithDependencies) =
     SwitchReservationForApplicationForm(
     reservationWithDependencies.id,
     reservationWithDependencies.reserverId,
     reservationWithDependencies.boatId,
     reservationWithDependencies.lengthCm,
     reservationWithDependencies.widthCm,
     reservationWithDependencies.amenity,
     reservationWithDependencies.type,
     reservationWithDependencies.place,
     reservationWithDependencies.locationName,
     reservationWithDependencies.validity,
     reservationWithDependencies.startDate,
     reservationWithDependencies.endDate,
     reservationWithDependencies.priceCents,
     reservationWithDependencies.vatCents,
     reservationWithDependencies.netPriceCents,
     reservationWithDependencies.created,
     reservationWithDependencies.excludedBoatTypes,
     reservationWithDependencies.section,
     reservationWithDependencies.storageType,
     reservationWithDependencies.originalReservationId.toString(),
     )

     fun buildRenewForm(
     citizenId: UUID,
     renewedReservation: ReservationWithDependencies,
     formInput: RenewalReservationInput,
     ): String {
     val htmlParams =
     buildBoatSpaceSwitchViewParams(citizenId, renewedReservation, formInput)
     if (renewedReservation.type == BoatSpaceType.Winter) {
     return boatSpaceSwitchForm.boatSpaceSwitchFormForWinterStorage(
     htmlParams
     )
     }
     return boatSpaceSwitchForm.boatSpaceRenewFormForSlip(
     htmlParams
     )
     }
     **/
}
