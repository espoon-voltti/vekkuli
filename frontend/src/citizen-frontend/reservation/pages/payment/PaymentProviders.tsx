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
      {({ providers, paymentRequired }) => paymentRequired ? (
        <div className="columns is-multiline" data-testid="payment-providers">
          {providers.map((provider) => (
            <PaymentButton
              key={`payment-provider-${provider.id}-${provider.name}`}
              paymentInformation={provider}
            />
          ))}
        </div>
      ) : (<div>Ei tarvitse maksaaa</div>)}
    </Loader>
  )
})
