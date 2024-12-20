import { query } from 'lib-common/query'

import {
  citizenActiveReservations,
  citizenExpiredReservations
} from '../api-clients/citizen'
import { createQueryKeys } from '../query'

export const queryKeys = createQueryKeys('citizen', {
  citizenActiveReservations: () => ['citizenActiveReservations'],
  citizenExpiredReservations: () => ['citizenExpiredReservations']
})

export const citizenActiveReservationsQuery = query({
  api: citizenActiveReservations,
  queryKey: queryKeys.citizenActiveReservations
})

export const citizenExpiredReservationsQuery = query({
  api: citizenExpiredReservations,
  queryKey: queryKeys.citizenExpiredReservations
})
