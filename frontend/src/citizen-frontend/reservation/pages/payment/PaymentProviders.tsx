import { Loader } from 'lib-components/Loader'
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

  return (
    <Loader results={[paymentProviders]}>
      {({ providers }) => (
        <div className="columns is-multiline">
          {providers.map((provider) => (
            <PaymentButton
              key={`payment-provider-${provider.id}-${provider.name}`}
              paymentInformation={provider}
            />
          ))}
        </div>
      )}
    </Loader>
  )
})
