import { fillReservation } from 'citizen-frontend/api-clients/reservation'
import { FillBoatSpaceReservationInput } from 'citizen-frontend/api-types/reservation'
import { mutation } from 'lib-common/query'

import { queryKeys } from '../../queries'

export const fillBoatSpaceReservationMutation = mutation({
  api: ({ id, input }: { id: number; input: FillBoatSpaceReservationInput }) =>
    fillReservation(id, input),
  resetQueryKeys: () => [
    queryKeys.unfinishedReservation(),
    queryKeys.unfinishedReservationExpiration()
  ]
})
