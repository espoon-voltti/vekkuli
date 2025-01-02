import { Loader } from 'lib-components/Loader'
import { Container, Section } from 'lib-components/dom'
import React, { useContext } from 'react'

import { useQueryResult } from 'lib-common/query'

import {
  citizenBoatsQuery,
  citizenOrganizationsQuery
} from '../../../shared/queries'
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
    <Section>
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
              <Form
                reservation={loadedReservation}
                boats={loadedBoats}
                municipalities={loadedMunicipalities}
                organizations={organizations}
              />
            </>
          )}
        </Loader>
      </Container>
    </Section>
  )
})
