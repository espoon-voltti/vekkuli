import React from 'react'

import { useQueryResult } from 'lib-common/query'

import PaymentButton from './PaymentButton'
import { paymentInformationQuery } from './queries'

type PaymentProvidersProps = {
  reservationId: number
}

export default React.memo(function PaymentProviders({
  reservationId
}: PaymentProvidersProps) {
  const paymentProviders = useQueryResult(
    paymentInformationQuery(reservationId)
  )

  if (paymentProviders.isLoading) {
    return <div>Loading...</div>
  }

  if (paymentProviders.isFailure) {
    return <div>Error...</div>
  }
  const { providers } = paymentProviders.value
  return (
    <div className="columns is-multiline">
      {providers.map((provider) => (
        <PaymentButton key={provider.id} paymentInformation={provider} />
      ))}
    </div>
  )
})
