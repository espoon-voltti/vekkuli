import {CreationType} from "../shared/types";
import {BoatSpaceReservation, RevisedPrice} from "../api-types/reservation";

export type RevisedPriceForReservation = Omit<RevisedPrice, "id"> & {
    creationType: CreationType
}

export const getRevisedPriceForReservation = (
    reservation: BoatSpaceReservation,
    revisedPrice: RevisedPrice): RevisedPriceForReservation => {

    return {
        ...revisedPrice,
        creationType: reservation.creationType
    }

}
