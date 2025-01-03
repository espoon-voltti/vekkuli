import {
  citizenBoats,
  citizenOrganizations
} from 'citizen-frontend/api-clients/citizen'
import { query } from 'lib-common/query'

import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('shared', {
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
