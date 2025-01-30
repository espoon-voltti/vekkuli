import { updateBoat } from 'citizen-frontend/api-clients/boat'
import { citizenOrganizations } from 'citizen-frontend/api-clients/citizen'
import { updateTrailer } from 'citizen-frontend/api-clients/trailer'
import { queryKeys } from 'citizen-frontend/citizen/queries'
import { queryKeys as sharedQueryKeys } from 'citizen-frontend/shared/queries'
import { mutation, query } from 'lib-common/query'

export const citizenOrganizationQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.citizenOrganizations
})

export const updateOrganizationBoatMutation = mutation({
  api: updateBoat,
  invalidateQueryKeys: () => [
    queryKeys.organizationActiveReservations(),
    sharedQueryKeys.organizationBoats()
  ]
})

export const updateOrganizationTrailerMutation = mutation({
  api: updateTrailer,
  invalidateQueryKeys: () => [queryKeys.citizenOrganizations()]
})
