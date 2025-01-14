import { Loader } from 'lib-components/Loader'
import { Container, MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'

import {
  citizenBoatsQuery,
  citizenOrganizationsQuery
} from 'citizen-frontend/shared/queries'
import { FormErrorProvider } from 'lib-common/form/state'
import { useQueryResult } from 'lib-common/query'

import StepIndicator from '../../StepIndicator'
import ReservationCancel from '../../components/ReservationCancel'
import ReservationTimer from '../../components/ReservationTimer'
import { getMunicipalitiesQuery } from '../../queries'
import { ReservationStateContext } from '../../state'

import Form from './Form'

export default React.memo(function FormPage() {
  const { reservation } = useContext(ReservationStateContext)
  const citizenBoats = useQueryResult(citizenBoatsQuery())
  const municipalities = useQueryResult(getMunicipalitiesQuery())
  const organizations = useQueryResult(citizenOrganizationsQuery())

  return (
    <MainSection>
      <Container>
        <Loader
          results={[reservation, citizenBoats, municipalities, organizations]}
        >
          {(
            loadedReservation,
            loadedBoats,
            loadedMunicipalities,
            organizations
          ) => (
            <>
              <Container>
                <ReservationCancel
                  reservationId={loadedReservation.id}
                  type="link"
                >
                  Takaisin
                </ReservationCancel>
              </Container>
              <StepIndicator step="fillInformation" />
              <ReservationTimer />
              <FormErrorProvider>
                <Form
                  reservation={loadedReservation}
                  boats={loadedBoats}
                  municipalities={loadedMunicipalities}
                  organizations={organizations}
                />
              </FormErrorProvider>
            </>
          )}
        </Loader>
      </Container>
    </MainSection>
  )
})
