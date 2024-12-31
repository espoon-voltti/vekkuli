import { getFreeSpaces } from 'citizen-frontend/api-clients/free-spaces'
import { reserveSpace } from 'citizen-frontend/api-clients/reservation'
import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
import { createQueryKeys } from 'citizen-frontend/query'
import { mutation, query } from 'lib-common/query'

const queryKeys = createQueryKeys('free-spaces', {
  allSearchesToFreeSpaces: () => ['searchFreeSpaces'],
  searchFreeSpaces: (params: SearchFreeSpacesParams | undefined) => [
    'searchFreeSpaces',
    params
  ]
})

export const freeSpacesQuery = query({
  api: getFreeSpaces,
  queryKey: queryKeys.searchFreeSpaces
})

export const reserveSpaceMutation = mutation({
  api: reserveSpace,
  invalidateQueryKeys: () => [queryKeys.allSearchesToFreeSpaces()]
})
