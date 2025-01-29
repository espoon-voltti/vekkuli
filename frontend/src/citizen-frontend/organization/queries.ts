import { citizenOrganizations } from 'citizen-frontend/api-clients/citizen'
import { updateTrailer } from 'citizen-frontend/api-clients/trailer'
import { queryKeys } from 'citizen-frontend/citizen/queries'
import { mutation, query } from 'lib-common/query'

export const citizenOrganizationQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.citizenOrganizations
})

export const updateOrganizationTrailerMutation = mutation({
  api: updateTrailer,
  invalidateQueryKeys: () => [queryKeys.citizenOrganizations()]
})
