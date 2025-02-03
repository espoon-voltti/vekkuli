import { deleteBoat, updateBoat } from 'citizen-frontend/api-clients/boat'
import {
  citizenOrganizationContactDetails,
  citizenOrganizations
} from 'citizen-frontend/api-clients/citizen'
import { citizenOrganizations } from 'citizen-frontend/api-clients/citizen'
import { terminateReservation } from 'citizen-frontend/api-clients/reservation'
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

export const deleteOrganizationBoatMutation = mutation({
  api: deleteBoat,
  invalidateQueryKeys: () => [sharedQueryKeys.organizationBoats()]
})

export const updateOrganizationTrailerMutation = mutation({
  api: updateTrailer,
  invalidateQueryKeys: () => [queryKeys.citizenOrganizations()]
})

export const citizenOrganizationContactDetailsQuery = query({
  api: citizenOrganizationContactDetails,
  queryKey: queryKeys.organizationContactDetails
})

export const terminateOrganizationReservationMutation = mutation({
  api: terminateReservation,
  invalidateQueryKeys: () => [
    queryKeys.organizationExpiredReservations(),
    queryKeys.organizationActiveReservations()
  ]
})
