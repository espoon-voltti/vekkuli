package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.formatAsFullDate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

enum class EmailType {
    ExpiredReservation,
    Expiry,
    Renew
}

@Service
class ScheduledSendEmailService(
    private val templateEmailService: TemplateEmailService,
    private val reservationService: BoatReservationService,
    private val organizationService: OrganizationService,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val emailEnv: EmailEnv,
    private val messageUtil: MessageUtil,
) {
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun sendReservationRenewReminderEmails() {
        val expiringIndefiniteReservations = reservationService.getExpiringIndefiniteBoatSpaceReservations()

        expiringIndefiniteReservations.forEach { reservation ->
            val recipients = getRecipients(reservation)
            val boatSpace =
                boatSpaceRepository.getBoatSpace(reservation.boatSpaceId)
                    ?: throw BadRequest("Boat space ${reservation.boatSpaceId} not found")
            val sender = emailEnv.senderAddress
            templateEmailService.sendBatchEmail(
                "boat_reservation_renew_reminder",
                null,
                sender,
                recipients,
                ReservationType.Marine,
                reservation.id,
                EmailType.Renew,
                mapOf(
                    "name" to "${reservation.locationName} ${reservation.place}",
                    "endDate" to formatAsFullDate(reservation.endDate.toLocalDate()),
                    "reserverName" to reservation.name,
                    "width" to
                        fi.espoo.vekkuli.utils
                            .intToDecimal(boatSpace.widthCm),
                    "length" to
                        fi.espoo.vekkuli.utils
                            .intToDecimal(boatSpace.lengthCm),
                ) +
                    messageUtil.getLocalizedMap("amenity", "boatSpaces.amenityOption.${boatSpace.amenity}") +
                    messageUtil.getLocalizedMap(
                        "placeType",
                        "boatSpaceReservation.email.types.${reservation.type}"
                    ) +
                    getHarborAddressLocalization(reservation, boatSpace.locationAddress ?: "")
            )
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun sendReservationExpiryReminderEmails() {
        val expiringFixedTermReservations = reservationService.getExpiringFixedTermBoatSpaceReservations()
        expiringFixedTermReservations.forEach { reservation ->
            val recipients = getRecipients(reservation)
            val sender = emailEnv.senderAddress
            templateEmailService.sendBatchEmail(
                "fixed_term_reservation_expiring",
                null,
                sender,
                recipients,
                ReservationType.Marine,
                reservation.id,
                EmailType.Expiry,
                mapOf(
                    "name" to "${reservation.locationName} ${reservation.place}",
                    "endDate" to formatAsFullDate(reservation.endDate.toLocalDate()),
                    "reserverName" to reservation.name,
                ) +
                    messageUtil.getLocalizedMap("placeType", "boatSpaceReservation.email.types.${reservation.type}")
            )
        }
    }

    private fun getHarborAddressLocalization(
        reservation: BoatSpaceReservationDetails,
        locationAddress: String
    ): Map<String, String> {
        val code =
            if (reservation.type == BoatSpaceType.Storage) {
                "boatSpaceReservation.email.storagePlaceAddress"
            } else {
                "boatSpaceReservation.email.harborAddress"
            }

        return messageUtil.getLocalizedMap("harborAddress", code, listOf(locationAddress))
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun sendReservationExpiredEmails() {
        val expiringFixedTermReservations = reservationService.getExpiredBoatSpaceReservations()
        expiringFixedTermReservations.forEach { reservation ->
            val recipients = getRecipients(reservation)
            val sender = emailEnv.senderAddress
            val placeName = "${reservation.locationName} ${reservation.place}"
            val reserverName = reservation.name
            templateEmailService.sendBatchEmail(
                "expired_reservation",
                null,
                sender,
                recipients,
                ReservationType.Marine,
                reservation.id,
                EmailType.ExpiredReservation,
                mapOf(
                    "name" to placeName,
                    "endDate" to formatAsFullDate(reservation.endDate.toLocalDate()),
                    "reserverName" to reserverName,
                ) +
                    messageUtil.getLocalizedMap("placeType", "boatSpaceReservation.email.types.${reservation.type}")
            )

            if (reservation.type == BoatSpaceType.Storage) {
                val recipient = Recipient(null, emailEnv.employeeAddress)
                val contactDetails = listOf(recipient)
                templateEmailService.sendBatchEmail(
                    "storage_place_expired_to_employee",
                    null,
                    sender,
                    contactDetails,
                    mapOf(
                        "name" to placeName,
                        "endDate" to formatAsFullDate(reservation.endDate.toLocalDate()),
                        "reserverName" to reserverName,
                        "reserverEmail" to reservation.email
                    )
                )
            }
        }
    }

    private fun getRecipients(reservation: BoatSpaceReservationDetails) =
        if (reservation.reserverType == ReserverType.Organization) {
            organizationService
                .getOrganizationMembers(reservation.reserverId)
                .map { Recipient(it.id, it.email) } + listOf(Recipient(reservation.reserverId, reservation.email))
        } else {
            listOf(Recipient(reservation.reserverId, reservation.email))
        }
}
