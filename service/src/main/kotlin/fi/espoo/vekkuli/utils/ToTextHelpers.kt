package fi.espoo.vekkuli.utils

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReservationWithDependencies

fun reservationStatusToText(reservationStatus: ReservationStatus): String =
    when (reservationStatus) {
        ReservationStatus.Info -> "Info"
        ReservationStatus.Payment -> "Maksettavana"
        ReservationStatus.Confirmed -> "Maksettu"
        ReservationStatus.Invoiced -> "Laskutettavana"
        ReservationStatus.Cancelled -> "Irtisanottu"
    }

fun placeTypeToText(placeType: BoatSpaceType?): String =
    when (placeType) {
        BoatSpaceType.Storage -> "Säilytyspaikka"
        BoatSpaceType.Slip -> "Laituripaikka"
        BoatSpaceType.Trailer -> "Traileripaikka"
        BoatSpaceType.Winter -> "Talvipaikka"
        else -> ""
    }

fun amenityToText(amenity: BoatSpaceAmenity?): String =
    when (amenity) {
        BoatSpaceAmenity.None -> ""
        BoatSpaceAmenity.RearBuoy -> "Peräpoiju"
        BoatSpaceAmenity.Beam -> "Aisa"
        BoatSpaceAmenity.WalkBeam -> "Kävelyaisa"
        BoatSpaceAmenity.Trailer -> "Traileri"
        BoatSpaceAmenity.Buck -> "Pukit"
        else -> ""
    }

fun boatTypeToText(boatType: BoatType?): String =
    when (boatType) {
        BoatType.OutboardMotor -> "Perämoottori"
        BoatType.InboardMotor -> "Sisämoottori"
        BoatType.Sailboat -> "Purjevene"
        BoatType.JetSki -> "Vesijetti"
        BoatType.Other -> "Muu"
        else -> ""
    }

fun ownershipStatusToText(ownershipStatus: OwnershipStatus?): String =
    when (ownershipStatus) {
        OwnershipStatus.Owner -> "Omistaja"
        OwnershipStatus.User -> "Haltija"
        OwnershipStatus.CoOwner -> "Kanssaomistaja"
        OwnershipStatus.FutureOwner -> "Tuleva omistaja"
        else -> ""
    }

fun terminationReasonToText(terminationReason: ReservationTerminationReason?): String =
    when (terminationReason) {
        ReservationTerminationReason.UserRequest -> "Toive"
        ReservationTerminationReason.InvalidOwner -> "Väärä omistaja"
        ReservationTerminationReason.RuleViolation -> "Sääntörikkomus"
        ReservationTerminationReason.PaymentViolation -> "Maksurikkomus"
        ReservationTerminationReason.Other -> "Muu"
        else -> ""
    }

fun boatSpaceTypeToText(boatSpaceType: BoatSpaceType?): String =
    when (boatSpaceType) {
        BoatSpaceType.Slip -> "Laituri"
        BoatSpaceType.Storage -> "Säilytys"
        BoatSpaceType.Trailer -> "Traileri"
        BoatSpaceType.Winter -> "Talvi"
        else -> ""
    }

fun paymentStatusToText(paymentStatus: PaymentStatus?): String =
    when (paymentStatus) {
        PaymentStatus.Created -> "Luotu"
        PaymentStatus.Success -> "Maksettu"
        PaymentStatus.Failed -> "Keskeytynyt"
        PaymentStatus.Refunded -> "Hyvitetty"
        else -> ""
    }

fun paymentTypeToText(paymentType: PaymentType?): String =
    when (paymentType) {
        PaymentType.OnlinePayment -> "Verkkomaksu"
        PaymentType.Invoice -> "Lasku"
        PaymentType.Other -> "Muu"
        else -> ""
    }

fun reservationCreationTypeToText(creationType: CreationType?): String =
    when (creationType) {
        CreationType.New -> "Uusi"
        CreationType.Renewal -> "Jatko"
        CreationType.Switch -> "Vaihto"
        else -> ""
    }

fun reservationValidityToText(validity: ReservationValidity?): String =
    when (validity) {
        ReservationValidity.FixedTerm -> "Määräaikainen"
        ReservationValidity.Indefinite -> "Jatkuva"
        else -> ""
    }

fun reservationToText(reservation: ReservationWithDependencies): String =
    "${reservation.locationName} ${boatSpaceTypeToText(reservation.type)} ${reservation.place}"

fun reservationToText(reservation: BoatSpaceReservationDetails): String =
    "${reservation.locationName} ${boatSpaceTypeToText(reservation.type)} ${reservation.place}"
