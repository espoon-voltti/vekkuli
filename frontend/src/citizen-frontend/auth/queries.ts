import { query } from 'lib-common/query'

import { getAuthStatus } from '../api-clients/auth'
import { createQueryKeys } from '../query'

const queryKeys = createQueryKeys('auth', {
  authStatus: () => ['status']
})

export const authStatusQuery = query({
  api: getAuthStatus,
  queryKey: queryKeys.authStatus
})
