import { query } from 'lib-common/query'

import { paymentInformation } from '../../../api-clients/reservation'
import { createQueryKeys } from '../../../query'

const queryKeys = createQueryKeys('reservation', {
  paymentInformation: () => ['paymentInformation']
})

export const paymentInformationQuery = query({
  api: paymentInformation,
  queryKey: queryKeys.paymentInformation
})
