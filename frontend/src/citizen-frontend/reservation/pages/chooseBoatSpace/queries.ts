import { getFreeSpaces } from 'citizen-frontend/api-clients/free-spaces'
import {
  canReserveSpace,
  getReservationBeingSwitched,
  reserveSpace,
  startToSwitchBoatSpace
} from 'citizen-frontend/api-clients/reservation'
import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
import { createQueryKeys } from 'citizen-frontend/query'
import { mutation, query } from 'lib-common/query'

import { queryKeys as sharedQueryKeys } from '../../queries'

export const queryKeys = createQueryKeys('free-spaces', {
  allSearchesToFreeSpaces: () => ['searchFreeSpaces'],
  searchFreeSpaces: (params: SearchFreeSpacesParams | undefined) => [
    'searchFreeSpaces',
    params
  ],
  canReserveSpace: (spaceId: number) => ['canReserveSpace', spaceId],
  reservationBeingSwitched: (reservationId: number) => [
    'reservationBeingSwitched',
    reservationId
  ],
  allReservationBeingSwitched: () => ['reservationBeingSwitched']
})

export const freeSpacesQuery = query({
  api: getFreeSpaces,
  queryKey: queryKeys.searchFreeSpaces,
  options: { refetchOnWindowFocus: true }
})

export const reserveSpaceMutation = mutation({
  api: reserveSpace,
  invalidateQueryKeys: () => [
    queryKeys.allSearchesToFreeSpaces(),
    sharedQueryKeys.unfinishedReservation()
  ]
})

export const startSwitchSpaceMutation = mutation({
  api: startToSwitchBoatSpace,
  invalidateQueryKeys: () => [
    queryKeys.allSearchesToFreeSpaces(),
    sharedQueryKeys.unfinishedReservation()
  ]
})

export const canReserveSpaceQuery = query({
  api: canReserveSpace,
  queryKey: queryKeys.canReserveSpace
})

export const reservationBeingSwitchedQuery = query({
  api: getReservationBeingSwitched,
  queryKey: queryKeys.reservationBeingSwitched
})
