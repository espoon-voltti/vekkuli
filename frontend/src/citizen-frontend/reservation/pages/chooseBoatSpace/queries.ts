import { getFreeSpaces } from 'citizen-frontend/api-clients/free-spaces'
import {
  canReserveSpace,
  reserveSpace,
  startToSwitchBoatSpace,
  switchBoatSpace
} from 'citizen-frontend/api-clients/reservation'
import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
import { FillBoatSpaceReservationInput } from 'citizen-frontend/api-types/reservation'
import { createQueryKeys } from 'citizen-frontend/query'
import { mutation, query } from 'lib-common/query'

import { queryKeys } from '../../queries'

const reservationQueryKeys = createQueryKeys('free-spaces', {
  allSearchesToFreeSpaces: () => ['searchFreeSpaces'],
  searchFreeSpaces: (params: SearchFreeSpacesParams | undefined) => [
    'searchFreeSpaces',
    params
  ],
  canReserveSpace: (spaceId: number) => ['canReserveSpace', spaceId]
})

export const freeSpacesQuery = query({
  api: getFreeSpaces,
  queryKey: reservationQueryKeys.searchFreeSpaces
})

export const reserveSpaceMutation = mutation({
  api: reserveSpace,
  invalidateQueryKeys: () => [reservationQueryKeys.allSearchesToFreeSpaces()]
})

export const starSwitchSpaceMutation = mutation({
  api: startToSwitchBoatSpace,
  invalidateQueryKeys: () => [reservationQueryKeys.allSearchesToFreeSpaces()]
})

export const switchSpaceMutation = mutation({
  api: ({ id, input }: { id: number; input: FillBoatSpaceReservationInput }) =>
    switchBoatSpace(id, input),
  invalidateQueryKeys: () => [
    queryKeys.unfinishedReservation(),
    queryKeys.unfinishedReservationExpiration()
  ]
})

export const canReserveSpaceQuery = query({
  api: canReserveSpace,
  queryKey: reservationQueryKeys.canReserveSpace
})
