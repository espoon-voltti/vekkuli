import {
  startRenewReservation,
  updateStorageType
} from 'citizen-frontend/api-clients/reservation'
import { UpdateStorageTypeRequest } from 'citizen-frontend/api-types/reservation'
import { UpdateStorageTypeInput } from 'citizen-frontend/api-types/storageType'
import { ReservationId } from 'citizen-frontend/shared/types'
import { mutation } from 'lib-common/query'

import { queryKeys } from '../../citizen/queries'
import { createMutationDisabledDefault } from '../util'

export const terminateReservationDisabled = createMutationDisabledDefault<
  ReservationId,
  void
>()

export const updateStorageTypeDisabled = createMutationDisabledDefault<
  UpdateStorageTypeRequest,
  void
>()

export const startRenewReservationMutation = mutation({
  api: startRenewReservation,
  invalidateQueryKeys: () => [queryKeys.unfinishedReservation()]
})

export const updateStorageTypeMutation = mutation({
  api: (params: {
    reservationId: ReservationId
    input: UpdateStorageTypeInput
  }) => updateStorageType(params),
  invalidateQueryKeys: () => [queryKeys.citizenActiveReservations()]
})
