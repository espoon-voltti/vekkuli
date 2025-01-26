import { Loader } from 'lib-components/Loader'
import { Container, MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'
import { useSearchParams } from 'react-router'

import { useTranslation } from 'citizen-frontend/localization'
import { ErrorBox } from 'citizen-frontend/reservation/components/ErrorBox'

import StepIndicator from '../../StepIndicator'
import { ReservationStateContext } from '../../state'

import CancelPayment from './CancelPayment'
import PaymentProviders from './PaymentProviders'

export default React.memo(function PaymentPage() {
  const { reservation } = useContext(ReservationStateContext)
  const i18n = useTranslation()
  const [searchParams] = useSearchParams()

  return (
    <MainSection dataTestId="payment-page">
      <Loader results={[reservation]}>
        {(unfinishedReservation) => (
          <>
            <Container>
              <CancelPayment reservation={unfinishedReservation.reservation} />
            </Container>
            <StepIndicator step="payment" />
            <div className="container">
              <h2>{i18n.payment.title}</h2>
              {searchParams.get('cancelled') === 'true' && (
                <ErrorBox
                  text={i18n.reservation.paymentPage.paymentCancelled}
                />
              )}
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
