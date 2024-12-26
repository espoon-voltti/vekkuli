import { Loader } from 'lib-components/Loader'
import React, { useContext } from 'react'

import { useQueryResult } from 'lib-common/query'

import {
  citizenBoatsQuery,
  citizenOrganizationsQuery
} from '../../../shared/queries'
import StepIndicator from '../../StepIndicator'
import { getMunicipalitiesQuery } from '../../queries'
import { ReservationStateContext } from '../../state'

import Form from './Form'

export default React.memo(function FormPage() {
  const { reservation } = useContext(ReservationStateContext)
  const citizenBoats = useQueryResult(citizenBoatsQuery())
  const municipalities = useQueryResult(getMunicipalitiesQuery())
  const organizations = useQueryResult(citizenOrganizationsQuery())

  return (
    <section className="section">
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
            <StepIndicator step="fillInformation" />
            <div className="container">
              <Form
                reservation={loadedReservation}
                boats={loadedBoats}
                municipalities={loadedMunicipalities}
                organizations={organizations}
              />
            </div>
          </>
        )}
      </Loader>
    </section>
  )
})
