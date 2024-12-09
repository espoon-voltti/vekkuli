import { query } from '../../lib-common/query'
import { citizenBoats } from '../api-clients/citizen'
import { createQueryKeys } from '../query'

const queryKeys = createQueryKeys('shared', {
  citizenBoats: () => ['citizenBoats']
})

export const citizenBoatsQuery = query({
  api: citizenBoats,
  queryKey: queryKeys.citizenBoats,
  options: { retry: false }
})
