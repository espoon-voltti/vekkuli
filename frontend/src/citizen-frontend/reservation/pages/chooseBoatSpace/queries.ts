import { getFreeSpaces } from 'citizen-frontend/api-clients/free-spaces'
import {
  canReserveSpace,
  reserveSpace,
  startToSwitchBoatSpace
} from 'citizen-frontend/api-clients/reservation'
import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
import { createQueryKeys } from 'citizen-frontend/query'
import { mutation, query } from 'lib-common/query'

import { queryKeys as sharedQueryKeys } from '../../queries'

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
  invalidateQueryKeys: () => [
    reservationQueryKeys.allSearchesToFreeSpaces(),
    sharedQueryKeys.unfinishedReservation()
  ]
})

export const startSwitchSpaceMutation = mutation({
  api: startToSwitchBoatSpace,
  invalidateQueryKeys: () => [
    reservationQueryKeys.allSearchesToFreeSpaces(),
    sharedQueryKeys.unfinishedReservation()
  ]
})

export const canReserveSpaceQuery = query({
  api: canReserveSpace,
  queryKey: reservationQueryKeys.canReserveSpace
})
