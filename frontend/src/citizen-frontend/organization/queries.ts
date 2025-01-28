import { updateCitizenTrailer } from 'citizen-frontend/api-clients/trailer'

import { mutation, query } from '../../lib-common/query'
import { citizenOrganizations } from '../api-clients/citizen'
import { queryKeys } from '../citizen/queries'

export const citizenOrganizationQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.citizenOrganizations
})

export const updateTrailerInformationMutation = mutation({
  api: updateCitizenTrailer,
  invalidateQueryKeys: () => [queryKeys.citizenOrganizations()]
})
