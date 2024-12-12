package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.reservationForm.*
import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.employee.InvoiceRow
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class BoatSpaceRenewalService(
    private val organizationService: OrganizationService,
    private val boatReservationService: BoatReservationService,
    private val reservationService: ReservationFormService,
    private val citizenService: CitizenService,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val seasonalService: SeasonalService,
    private val boatSpaceRenewForm: BoatSpaceRenewFormView,
    private val trailerRepo: TrailerRepository
) {
    fun getOrCreateRenewalReservationForEmployee(
        userId: UUID,
        originalReservationId: Int,
    ): ReservationWithDependencies {
        val renewal = boatSpaceRenewalRepository.getRenewalReservationForEmployee(userId, originalReservationId)
        if (renewal != null) return renewal

        val renewalReservation =
            createRenewalReservation(originalReservationId, userId, UserType.EMPLOYEE)
                ?: throw IllegalStateException("Reservation not found")
        return renewalReservation
    }

    fun getOrCreateRenewalReservationForCitizen(
        userId: UUID,
        originalReservationId: Int,
    ): ReservationWithDependencies {
        val renewal = boatSpaceRenewalRepository.getRenewalReservationForCitizen(userId, originalReservationId)
        if (renewal != null) return renewal

        val renewalReservation =
            createRenewalReservation(originalReservationId, userId, UserType.CITIZEN)
                ?: throw IllegalStateException("Reservation not found")
        return renewalReservation
    }

    fun getRenewalReservationForCitizen(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceRenewalRepository
            .getRenewalReservationForCitizen(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun getRenewalReservationForEmployee(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceRenewalRepository.getRenewalReservationForEmployee(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun cancelRenewalReservation(
        renewalReservationId: Int,
        citizenId: UUID
    ) {
        boatReservationService.removeBoatSpaceReservation(renewalReservationId, citizenId)
    }

    fun updateRenewReservation(
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
            citizenService.updateCitizen(
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
            functionInformation = "?",
            billingPeriodStart = "",
            billingPeriodEnd = "",
            boatingSeasonStart = LocalDate.of(2025, 5, 1),
            boatingSeasonEnd = LocalDate.of(2025, 9, 30),
            invoiceNumber = "",
            dueDate = LocalDate.of(2025, 12, 31),
            costCenter = "?",
            invoiceType = "?",
            invoiceRows =
                listOf(
                    InvoiceRow(
                        description = invoiceData.description,
                        customer = "${invoiceData.lastname} ${invoiceData.firstnames}",
                        priceWithoutVat = reservation.priceWithoutVatInEuro.toString(),
                        vat = reservation.vatPriceInEuro.toString(),
                        priceWithVat = reservation.priceInEuro.toString(),
                        organization = "Merellinen ulkoilu",
                        paymentDate = LocalDate.of(2025, 1, 1)
                    )
                )
        )
    }

    @Transactional
    fun activateRenewalAndSendInvoice(
        renewedReservationId: Int,
        reserverId: UUID?,
        renewedFromId: Int?
    ) {
        if (reserverId == null || renewedFromId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceData =
            invoiceService.createInvoiceData(renewedReservationId, reserverId)
                ?: throw InternalError("Failed to create invoice batch")

        boatReservationService.setReservationStatusToInvoiced(renewedReservationId)

        boatReservationService.markReservationEnded(renewedFromId)

        invoiceService.createAndSendInvoice(invoiceData, reserverId, renewedReservationId)
            ?: throw InternalError("Failed to send invoice")
    }

    fun buildBoatSpaceRenewalViewParams(
        citizenId: UUID,
        renewedReservation: ReservationWithDependencies,
        formInput: RenewalReservationInput,
    ): BoatSpaceRenewViewParams {
        val citizen = citizenService.getCitizen(citizenId)
        if (citizen == null || renewedReservation.reserverId != citizenId) {
            throw UnauthorizedException()
        }

        var input =
            formInput.copy(
                email = citizen?.email,
                phone = citizen?.phone,
                storageType =
                    renewedReservation.storageType ?: StorageType.None,
            )
        if (renewedReservation.trailerId != null) {
            val trailer = trailerRepo.getTrailer(renewedReservation.trailerId) ?: throw BadRequest("Trailer not found")
            input =
                input.copy(
                    trailerLength = trailer?.lengthCm?.cmToM(),
                    trailerWidth = trailer?.widthCm?.cmToM(),
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
                        depth = boat.depthCm.cmToM(),
                        boatName = boat.name,
                        weight = boat.weightKg,
                        width = boat.widthCm.cmToM(),
                        length = boat.lengthCm.cmToM(),
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
        return BoatSpaceRenewViewParams(
            renewedReservationForApplicationForm,
            boats,
            citizen,
            input,
            UserType.CITIZEN,
        )
    }

    private fun buildReservationForApplicationForm(reservationWithDependencies: ReservationWithDependencies) =
        RenewalReservationForApplicationForm(
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
            reservationWithDependencies.renewedFromId.toString(),
        )

    fun createRenewalReservation(
        originalReservationId: Int,
        userId: UUID,
        userType: UserType
    ): ReservationWithDependencies? {
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservation(originalReservationId)
                ?: throw BadRequest("Reservation to renew not found")
        if (!seasonalService.canRenewAReservation(reservation.validity, reservation.endDate).success) {
            throw Conflict("Reservation cannot be renewed")
        }
        val newId = boatSpaceRenewalRepository.createRenewalRow(originalReservationId, userType, userId)
        return boatSpaceReservationRepo.getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(newId)
    }

    fun buildRenewForm(
        citizenId: UUID,
        renewedReservation: ReservationWithDependencies,
        formInput: RenewalReservationInput,
    ): String {
        val htmlParams =
            buildBoatSpaceRenewalViewParams(citizenId, renewedReservation, formInput)
        if (renewedReservation.type == BoatSpaceType.Winter) {
            return boatSpaceRenewForm.boatSpaceRenewalFormForWinterStorage(
                htmlParams
            )
        }
        return boatSpaceRenewForm.boatSpaceRenewFormForSlip(
            htmlParams
        )
    }
}
