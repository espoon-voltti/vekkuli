import { BoatSpaceReservation, ReservationInfo } from '../api-types/reservation'
import { CreationType } from '../shared/types'

export type RevisedPriceForReservation = Omit<ReservationInfo, 'id'> & {
  creationType: CreationType
}

export const getRevisedPriceForReservation = (
  reservation: BoatSpaceReservation,
  reservationInfo: ReservationInfo
): RevisedPriceForReservation => {
  return {
    ...reservationInfo,
    creationType: reservation.creationType
  }
}
