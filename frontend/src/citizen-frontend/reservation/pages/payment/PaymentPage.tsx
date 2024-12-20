import { Loader } from 'lib-components/Loader'
import React, { useContext } from 'react'

import StepIndicator from '../../StepIndicator'
import { ReservationStateContext } from '../../state'

import PaymentProviders from './PaymentProviders'

export default React.memo(function PaymentPage() {
  const { reservation } = useContext(ReservationStateContext)

  return (
    <section className="section">
      <Loader results={reservation}>
        {(unfinishedReservation) => (
          <>
            <StepIndicator step="payment" />
            <div className="container">
              <h2>Espoon resurssivarausjärjestelmä</h2>
              <PaymentProviders reservationId={unfinishedReservation.id} />
            </div>
          </>
        )}
      </Loader>
    </section>
  )
})
