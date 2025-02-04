import { startRenewReservation } from 'citizen-frontend/api-clients/reservation'
import { ReservationId } from 'citizen-frontend/shared/types'
import { mutation } from 'lib-common/query'

import { queryKeys } from '../../citizen/queries'
import { createMutationDisabledDefault } from '../util'

export const terminateReservationDisabled = createMutationDisabledDefault<
  ReservationId,
  void
>()

export const startRenewReservationMutation = mutation({
  api: startRenewReservation,
  invalidateQueryKeys: () => [queryKeys.unfinishedReservation()]
})
