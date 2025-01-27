import {CreationType, NewOrganization, Organization, ReserverType} from "../../../shared/types";
import {BoatSpaceReservation} from "../../../api-types/reservation";

export type ReservationPriceInfo = {
    reserverType: ReserverType
    reserverName?: string
    discountPercentage: number
    priceInCents: number
    discountedPriceInCents: number
    creationType: CreationType
}

const discountedPrice = (priceInCents: number, discountPercentage: number) =>
    discountPercentage > 0 ? (priceInCents - (priceInCents * discountPercentage) / 100) : priceInCents

export const getReservationPriceInfo = (reservation: BoatSpaceReservation,
                                 organization: Organization | NewOrganization | null | undefined): ReservationPriceInfo => {
    const {revisedPrice, citizen, creationType} = reservation
    const [reserverType, discountPercentage, reserverName] = organization ? (
        [
            'Organization' as ReserverType,
            organization.discountPercentage,
            organization.name
        ]
    ) : citizen ? (
        [
            'Citizen' as ReserverType,
            citizen.discountPercentage,
            `${citizen.firstName} ${citizen.lastName}`
        ]
    ) : ['Citizen' as ReserverType,0]

    return {
        reserverType: reserverType,
        reserverName: reserverName,
        discountPercentage: discountPercentage,
        priceInCents: revisedPrice,
        discountedPriceInCents: discountedPrice(revisedPrice, discountPercentage),
        creationType: creationType
    }
}
