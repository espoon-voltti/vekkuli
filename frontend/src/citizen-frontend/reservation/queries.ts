import { mutation, query } from 'lib-common/query'

import {
  cancelReservation,
  municipalities,
  unfinishedReservation
} from '../api-clients/reservation'
import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('reservation', {
  unfinishedReservation: () => ['unfinishedReservation'],
  municipalities: () => ['municipalities']
})

export const unfinishedReservationQuery = query({
  api: unfinishedReservation,
  queryKey: queryKeys.unfinishedReservation,
  options: { retry: false }
})

export const cancelReservationMutation = mutation({
  api: cancelReservation,
  invalidateQueryKeys: () => [queryKeys.unfinishedReservation()]
})

export const getMunicipalitiesQuery = query({
  api: municipalities,
  queryKey: queryKeys.municipalities
})
