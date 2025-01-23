import {
  citizenBoats,
  organizationBoats
} from 'citizen-frontend/api-clients/citizen'
import { query } from 'lib-common/query'

import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('shared', {
  organizationBoats: () => ['organizationBoats'],
  citizenBoats: () => ['citizenBoats'],
  organizations: () => ['citizenOrganizations'],
  citizenOrganizationsBoats: () => ['citizenOrganizationsBoats']
})

export const citizenBoatsQuery = query({
  api: citizenBoats,
  queryKey: queryKeys.citizenBoats,
  options: { retry: false }
})

export const organizationBoatsQuery = query({
  api: organizationBoats,
  queryKey: queryKeys.organizationBoats,
  options: { retry: false }
})
