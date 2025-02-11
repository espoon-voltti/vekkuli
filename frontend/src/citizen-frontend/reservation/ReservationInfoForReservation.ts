import { BoatSpaceReservation, ReservationInfo } from '../api-types/reservation'
import { CreationType } from '../shared/types'

export type ReservationInfoForReservation = Omit<ReservationInfo, 'id'> & {
  creationType: CreationType
}

export const getReservationInfoForReservation = (
  reservation: BoatSpaceReservation,
  reservationInfo: ReservationInfo
): ReservationInfoForReservation => {
  return {
    ...reservationInfo,
    creationType: reservation.creationType
  }
}
