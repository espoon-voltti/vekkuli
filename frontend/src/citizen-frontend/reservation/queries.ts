import { mutation, query } from 'lib-common/query'

import {
  cancelReservation,
  unfinishedReservation
} from '../api-clients/reservation'
import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('reservation', {
  unfinishedReservation: () => ['unfinishedReservation']
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
