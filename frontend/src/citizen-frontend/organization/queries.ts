import { query } from '../../lib-common/query'
import { citizenOrganizations } from '../api-clients/citizen'
import { queryKeys } from '../citizen/queries'

export const citizenOrganizationQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.citizenOrganizations
})
