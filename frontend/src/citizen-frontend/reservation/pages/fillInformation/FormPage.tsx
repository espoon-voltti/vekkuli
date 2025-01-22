import { Loader } from 'lib-components/Loader'
import { Container, MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { citizenOrganizationsBoatsQuery } from 'citizen-frontend/shared/queries'
import { FormErrorProvider } from 'lib-common/form/state'
import { useQueryResult } from 'lib-common/query'

import StepIndicator from '../../StepIndicator'
import ReservationCancel from '../../components/ReservationCancel'
import ReservationTimer from '../../components/ReservationTimer'
import { ReservationStateContext } from '../../state'

import Form from './Form'

export default React.memo(function FormPage() {
  const { reservation } = useContext(ReservationStateContext)
  const organizationBoats = useQueryResult(citizenOrganizationsBoatsQuery())
  const i18n = useTranslation()
  return (
    <MainSection>
      <Container>
        <Loader results={[reservation, organizationBoats]}>
          {(loadedReservation, organizationBoats) => (
            <>
              <Container>
                <ReservationCancel
                  reservationId={loadedReservation.reservation.id}
                  type="link"
                  buttonAriaLabel={i18n.reservation.cancelAndGoBack}
                >
                  {i18n.components.links.goBack}
                </ReservationCancel>
              </Container>
              <StepIndicator step="fillInformation" />
              <ReservationTimer />
              <FormErrorProvider>
                <Form
                  reservation={loadedReservation}
                  organizationBoats={organizationBoats}
                />
              </FormErrorProvider>
            </>
          )}
        </Loader>
      </Container>
    </MainSection>
  )
})
