import React from 'react'

import StepIndicator from '../../StepIndicator'
import { useReservationState } from '../../state'

import PaymentProviders from './PaymentProviders'

export default React.memo(function PaymentPage() {
  const unfinishedReservation = useReservationState()

  return (
    <section className="section">
      <StepIndicator step="payment" />
      <div className="container">
        <h2>Espoon resurssivarausjärjestelmä</h2>
        {!unfinishedReservation?.id ? null : (
          <PaymentProviders reservationId={unfinishedReservation.id} />
        )}
      </div>
    </section>
  )
})
