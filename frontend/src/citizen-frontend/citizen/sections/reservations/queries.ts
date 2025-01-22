import {
  startRenewReservation,
  terminateReservation
} from 'citizen-frontend/api-clients/reservation'
import { mutation } from 'lib-common/query'

import { queryKeys } from '../../queries'

export const terminateReservationMutation = mutation({
  api: terminateReservation,
  invalidateQueryKeys: () => [
    queryKeys.citizenActiveReservations(),
    queryKeys.citizenExpiredReservations()
  ]
})

export const startRenewReservationMutation = mutation({
  api: startRenewReservation,
  // TODO: what should be invalidated?
  invalidateQueryKeys: () => [
    queryKeys.citizenActiveReservations(),
    queryKeys.citizenExpiredReservations()
  ]
})
