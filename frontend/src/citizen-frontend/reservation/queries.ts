import { mutation, query } from 'lib-common/query'

import {
  cancelReservation,
  municipalities,
  unfinishedReservation,
  unfinishedReservationExpiration
} from '../api-clients/reservation'
import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('reservation', {
  unfinishedReservation: () => ['unfinishedReservation'],
  unfinishedReservationExpiration: () => ['unfinishedReservationExpiration'],
  municipalities: () => ['municipalities']
})

export const unfinishedReservationQuery = query({
  api: unfinishedReservation,
  queryKey: queryKeys.unfinishedReservation,
  options: { retry: false }
})

export const cancelReservationMutation = mutation({
  api: cancelReservation,
  resetQueryKeys: () => [
    queryKeys.unfinishedReservation(),
    queryKeys.unfinishedReservationExpiration()
  ]
})

export const getMunicipalitiesQuery = query({
  api: municipalities,
  queryKey: queryKeys.municipalities
})

export const unfinishedReservationExpirationQuery = query({
  api: unfinishedReservationExpiration,
  queryKey: queryKeys.unfinishedReservationExpiration,
  options: { retry: false }
})
