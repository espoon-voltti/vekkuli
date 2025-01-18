import {
  citizenBoats,
  organizationBoats,
  citizenOrganizations,
  citizenOrganizationsBoats
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

export const citizenOrganizationsQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.organizations
})

export const organizationBoatsQuery = query({
  api: organizationBoats,
  queryKey: queryKeys.organizationBoats,
  options: { retry: false }
})
export const citizenOrganizationsBoatsQuery = query({
  api: citizenOrganizationsBoats,
  queryKey: queryKeys.citizenOrganizationsBoats,
  options: { retry: false }
})
