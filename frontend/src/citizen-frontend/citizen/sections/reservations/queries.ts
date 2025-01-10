import { terminateReservation } from 'citizen-frontend/api-clients/reservation'
import { mutation } from 'lib-common/query'

import { queryKeys } from '../../queries'

export const terminateReservationMutation = mutation({
  api: terminateReservation,
  invalidateQueryKeys: () => [
    queryKeys.citizenActiveReservations(),
    queryKeys.citizenExpiredReservations()
  ]
})
