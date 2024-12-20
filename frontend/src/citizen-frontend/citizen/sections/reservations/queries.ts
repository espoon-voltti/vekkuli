import { mutation } from 'lib-common/query'

import { terminateReservation } from '../../../api-clients/reservation'
import { queryKeys } from '../../queries'

export const terminateReservationMutation = mutation({
  api: terminateReservation,
  invalidateQueryKeys: () => [queryKeys.citizenActiveReservations()]
})
