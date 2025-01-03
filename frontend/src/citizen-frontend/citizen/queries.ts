import {
  citizenActiveReservations,
  citizenExpiredReservations,
  citizenOrganizations,
  updateCitizenInformation
} from 'citizen-frontend/api-clients/citizen'
import { updateCitizenTrailer } from 'citizen-frontend/api-clients/trailer'
import { queryKeys as sharedQueryKeys } from 'citizen-frontend/shared/queries'
import { mutation, query } from 'lib-common/query'

import { updateCitizenBoat } from '../api-clients/boat'
import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('citizen', {
  citizenActiveReservations: () => ['citizenActiveReservations'],
  citizenExpiredReservations: () => ['citizenExpiredReservations'],
  citizenOrganizations: () => ['citizenOrganizations']
})

export const citizenActiveReservationsQuery = query({
  api: citizenActiveReservations,
  queryKey: queryKeys.citizenActiveReservations
})

export const citizenExpiredReservationsQuery = query({
  api: citizenExpiredReservations,
  queryKey: queryKeys.citizenExpiredReservations
})

export const citizenOrganizationQuery = query({
  api: citizenOrganizations,
  queryKey: queryKeys.citizenOrganizations
})

export const updateCitizenInformationMutation = mutation({
  api: updateCitizenInformation
})

export const updateTrailerInformationMutation = mutation({
  api: updateCitizenTrailer,
  invalidateQueryKeys: () => [queryKeys.citizenActiveReservations()]
})

export const updateBoatInformationMutation = mutation({
  api: updateCitizenBoat,
  invalidateQueryKeys: () => [
    queryKeys.citizenActiveReservations(),
    sharedQueryKeys.citizenBoats()
  ]
})
