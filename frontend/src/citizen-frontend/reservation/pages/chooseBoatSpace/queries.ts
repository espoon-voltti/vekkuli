import { mutation, query } from 'lib-common/query'

import { getFreeSpaces } from '../../../api-clients/free-spaces'
import { reserveSpace } from '../../../api-clients/reservation'
import { SearchFreeSpacesParams } from '../../../api-types/free-spaces'
import { createQueryKeys } from '../../../query'

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
