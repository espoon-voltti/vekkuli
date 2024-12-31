import { mutation, query } from 'lib-common/query'

import {
  citizenActiveReservations,
  citizenExpiredReservations,
  citizenOrganizations,
  updateCitizenInformation
} from '../api-clients/citizen'
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
