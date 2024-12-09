import { mutation } from 'lib-common/query'

import { fillReservation } from '../../../api-clients/reservation'
import { FillBoatSpaceReservationInput } from '../../../api-types/reservation'
import { queryKeys } from '../../queries'

export const fillBoatSpaceReservationMutation = mutation({
  api: ({ id, input }: { id: number; input: FillBoatSpaceReservationInput }) =>
    fillReservation(id, input),
  invalidateQueryKeys: () => [queryKeys.unfinishedReservation()]
})
