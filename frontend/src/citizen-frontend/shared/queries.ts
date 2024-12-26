import { query } from 'lib-common/query'

import { citizenBoats, citizenOrganizations } from '../api-clients/citizen'
import { createQueryKeys } from '../query'

const queryKeys = createQueryKeys('shared', {
  citizenBoats: () => ['citizenBoats'],
  organizations: () => ['citizenOrganizations']
})

export const citizenBoatsQuery = query({
  api: citizenBoats,
  queryKey: queryKeys.citizenBoats,
  options: { retry: false }
})

export const citizenOrganizationsQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.organizations
})
