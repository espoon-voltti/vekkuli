import {
  cancelPayment,
  paymentInformation
} from 'citizen-frontend/api-clients/reservation'
import { createQueryKeys } from 'citizen-frontend/query'
import { mutation, query } from 'lib-common/query'

import { queryKeys as reservationQueryKeys } from '../../queries'

const queryKeys = createQueryKeys('reservation', {
  paymentInformation: () => ['paymentInformation']
})

export const paymentInformationQuery = query({
  api: paymentInformation,
  queryKey: queryKeys.paymentInformation
})

export const cancelPaymentMutation = mutation({
  api: cancelPayment,
  invalidateQueryKeys: () => [reservationQueryKeys.unfinishedReservation()]
})
