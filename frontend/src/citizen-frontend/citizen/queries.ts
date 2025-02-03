import { deleteBoat, updateBoat } from 'citizen-frontend/api-clients/boat'
import {
  citizenActiveReservations,
  citizenExpiredReservations,
  citizenOrganizations,
  organizationActiveReservations,
  organizationExpiredReservations,
  updateCitizenInformation
} from 'citizen-frontend/api-clients/citizen'
import { terminateReservation } from 'citizen-frontend/api-clients/reservation'
import { updateTrailer } from 'citizen-frontend/api-clients/trailer'
import { queryKeys as sharedQueryKeys } from 'citizen-frontend/shared/queries'
import { mutation, query } from 'lib-common/query'

import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('citizen', {
  citizenActiveReservations: () => ['citizenActiveReservations'],
  organizationActiveReservations: () => ['organizationActiveReservations'],
  citizenExpiredReservations: () => ['citizenExpiredReservations'],
  organizationExpiredReservations: () => ['organizationExpiredReservations'],
  citizenOrganizations: () => ['citizenOrganizations'],
  unfinishedReservation: () => ['unfinishedReservation'],
  allSearchesToFreeSpaces: () => ['searchFreeSpaces']
})

export const citizenActiveReservationsQuery = query({
  api: citizenActiveReservations,
  queryKey: queryKeys.citizenActiveReservations
})

export const organizationActiveReservationsQuery = query({
  api: organizationActiveReservations,
  queryKey: queryKeys.organizationActiveReservations
})

export const citizenExpiredReservationsQuery = query({
  api: citizenExpiredReservations,
  queryKey: queryKeys.citizenExpiredReservations
})

export const organizationExpiredReservationsQuery = query({
  api: organizationExpiredReservations,
  queryKey: queryKeys.organizationExpiredReservations
})

export const citizenOrganizationQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.citizenOrganizations
})

export const updateCitizenInformationMutation = mutation({
  api: updateCitizenInformation
})

export const updateCitizenTrailerMutation = mutation({
  api: updateTrailer,
  invalidateQueryKeys: () => [queryKeys.citizenActiveReservations()]
})

export const updateCitizenBoatMutation = mutation({
  api: updateBoat,
  invalidateQueryKeys: () => [
    queryKeys.citizenActiveReservations(),
    sharedQueryKeys.citizenBoats()
  ]
})

export const deleteCitizenBoatMutation = mutation({
  api: deleteBoat,
  invalidateQueryKeys: () => [sharedQueryKeys.citizenBoats()]
})

export const terminateCitizenReservationMutation = mutation({
  api: terminateReservation,
  invalidateQueryKeys: () => [
    queryKeys.citizenActiveReservations(),
    queryKeys.citizenExpiredReservations()
  ]
})
