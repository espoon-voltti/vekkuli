import { Loader } from 'lib-components/Loader'
import { Container, MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'

import StepIndicator from '../../StepIndicator'
import { ReservationStateContext } from '../../state'

import CancelPayment from './CancelPayment'
import PaymentProviders from './PaymentProviders'

export default React.memo(function PaymentPage() {
  const { reservation } = useContext(ReservationStateContext)

  return (
    <MainSection>
      <Loader results={[reservation]}>
        {(unfinishedReservation) => (
          <>
            <Container>
              <CancelPayment reservation={unfinishedReservation.reservation} />
            </Container>
            <StepIndicator step="payment" />
            <div className="container">
              <h2>Espoon resurssivarausjärjestelmä</h2>
              <PaymentProviders
                reservationId={unfinishedReservation.reservation.id}
              />
            </div>
          </>
        )}
      </Loader>
    </MainSection>
  )
})
