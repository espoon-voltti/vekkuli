import { query } from 'lib-common/query'

import { getReservation } from '../../../api-clients/reservation'
import { createQueryKeys } from '../../../query'

const queryKeys = createQueryKeys('reservation', {
  getReservation: () => ['getReservation']
})

export const getReservationQuery = query({
  api: getReservation,
  queryKey: queryKeys.getReservation
})
