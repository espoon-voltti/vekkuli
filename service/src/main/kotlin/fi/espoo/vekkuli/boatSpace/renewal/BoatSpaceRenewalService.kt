package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateBoatSpaceReservationService
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UnauthorizedException
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.controllers.getReservationTimeInSeconds
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.employee.InvoiceRow
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class BoatSpaceRenewalService(
    private val organizationService: OrganizationService,
    private val reservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val timeProvider: TimeProvider,
    private val terminateBoatSpaceReservationService: TerminateBoatSpaceReservationService,
) {
    fun getOrCreateRenewalReservationForEmployee(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies {
        val renewal = boatSpaceRenewalRepository.getRenewalReservationForEmployee(userId)
        val renewalReservation = renewal ?: reservationService.createRenewalReservationForEmployee(reservationId, userId)
        if (renewalReservation == null) throw IllegalStateException("Reservation not found")
        return renewalReservation
    }

    fun getOrCreateRenewalReservationForCitizen(
        userId: UUID,
        oldReservationId: Int,
    ): ReservationWithDependencies {
        val renewal = boatSpaceRenewalRepository.getRenewalReservationForCitizen(userId)

        val renewalReservation = renewal ?: reservationService.createRenewalReservationForCitizen(oldReservationId, userId)
        if (renewalReservation == null) throw IllegalStateException("Reservation not found")
        return renewalReservation
    }

    fun updateRenewReservation(
        citizenId: UUID,
        input: RenewalReservationInput,
        reservationId: Int
    ) {
        val reservation =
            reservationService.getReservationWithReserver(reservationId)
                ?: throw NotFound("Reservation not found")

        if (reservation.reserverId == null) {
            throw UnauthorizedException()
        }
        updateReserver(reservation.reserverType, reservation.reserverId, input)

        reservationService.reserveBoatSpace(
            reserverId = reservation.reserverId,
            ReserveBoatSpaceInput(
                reservationId = reservationId,
                boatId = input.boatId,
                boatType = input.boatType!!,
                width = input.width ?: 0.0,
                length = input.length ?: 0.0,
                depth = input.depth ?: 0.0,
                weight = input.weight,
                boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                boatName = input.boatName ?: "",
                otherIdentification = input.otherIdentification ?: "",
                extraInformation = input.extraInformation ?: "",
                ownerShip = input.ownership!!,
                email = input.email!!,
                phone = input.phone!!,
            ),
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
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceData = invoiceService.createInvoiceData(reservationId, reservation.reserverId)
        if (invoiceData == null) {
            throw IllegalArgumentException("Failed to create invoice data")
        }

        // TODO: get the actual data
        return SendInvoiceModel(
            reservationId = reservationId,
            reserverName = "${invoiceData.firstnames} ${invoiceData.lastname}",
            reserverSsn = invoiceData.ssn,
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

    fun activateRenewalAndSendInvoice(renewedReservationId: Int) {
        val newReservation = reservationService.getReservationWithReserver(renewedReservationId)
        if (newReservation?.reserverId == null || newReservation.renewedFromId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceData =
            invoiceService.createInvoiceData(renewedReservationId, newReservation.reserverId)
                ?: throw InternalError("Failed to create invoice batch")

        invoiceService.createAndSendInvoice(invoiceData, newReservation.reserverId, newReservation.id)
            ?: throw InternalError("Failed to create invoice")

        reservationService.setReservationStatusToInvoiced(newReservation.id)

        reservationService.markReservationEnded(newReservation.renewedFromId)
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

        var input = formInput.copy(email = citizen?.email, phone = citizen?.phone)
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

        val municipalities = citizenService.getMunicipalities()
        return BoatSpaceRenewViewParams(
            renewedReservation,
            boats,
            citizen,
            input,
            getReservationTimeInSeconds(renewedReservation.created, timeProvider.getCurrentDateTime()),
            UserType.CITIZEN,
            municipalities
        )
    }
}
