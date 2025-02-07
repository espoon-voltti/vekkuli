import { getReservation } from 'citizen-frontend/api-clients/reservation'
import { query } from 'lib-common/query'

import { createQueryKeys } from '../../../query'

const queryKeys = createQueryKeys('reservation', {
  getReservation: () => ['getReservation']
})

export const getReservationQuery = query({
  api: getReservation,
  queryKey: queryKeys.getReservation
})
