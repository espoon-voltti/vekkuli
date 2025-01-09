import { paymentInformation } from 'citizen-frontend/api-clients/reservation'
import { createQueryKeys } from 'citizen-frontend/query'
import { query } from 'lib-common/query'

const queryKeys = createQueryKeys('reservation', {
  paymentInformation: () => ['paymentInformation']
})

export const paymentInformationQuery = query({
  api: paymentInformation,
  queryKey: queryKeys.paymentInformation
})
