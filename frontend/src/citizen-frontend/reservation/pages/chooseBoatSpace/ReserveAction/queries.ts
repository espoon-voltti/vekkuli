import { getBoatSpace } from 'citizen-frontend/api-clients/boat-space'
import { createQueryKeys } from 'citizen-frontend/query'
import { query } from 'lib-common/query'

export const queryKeys = createQueryKeys('reserve-action', {
  boatSpace: (spaceId: number) => ['getBoatSpace', spaceId]
})

export const boatSpaceQuery = query({
  api: getBoatSpace,
  queryKey: queryKeys.boatSpace,
  options: { retry: false }
})
