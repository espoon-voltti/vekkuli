import { Loader } from 'lib-components/Loader'
import { MainSection } from 'lib-components/dom'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { ReservationError } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import { formatPlaceIdentifier } from 'citizen-frontend/shared/formatters'
import { useQueryResult } from 'lib-common/query'
import useRouteParams from 'lib-common/useRouteParams'

import { getReservationQuery } from './queries'

export default React.memo(function ErrorPage() {
  const i18n = useTranslation()
  const { reservationId, error } = useRouteParams(['reservationId', 'error'])

  const reservation = useQueryResult(
    getReservationQuery(parseInt(reservationId))
  )

  return (
    <MainSection
      dataTestId="error-page"
      ariaLabel={i18n.reservation.steps.error}
    >
      <div className="container">
        <h2 className="h1">
          {i18n.reservation.errors.failedReservation.title}
        </h2>
        <div className="container">
          <ErrorMessage error={error} />
        </div>
        <Loader results={[reservation]}>
          {({ boatSpace }) => (
            <div className="form-section">
              <h3 className="header">
                {i18n.reservation.formPage.boatSpaceInformation}
              </h3>
              <div className="columns">
                <div className="column is-one-quarter">
                  <TextField
                    label={i18n.reservation.formPage.harbor}
                    value={
                      boatSpace.locationId
                        ? i18n.boatSpace.harbors[boatSpace.locationId]
                        : '-'
                    }
                    readonly={true}
                  />
                </div>
                <div className="column is-one-quarter">
                  <TextField
                    label={i18n.reservation.formPage.place}
                    value={formatPlaceIdentifier(
                      boatSpace.section,
                      boatSpace.placeNumber
                    )}
                    readonly={true}
                  />
                </div>
                <div className="column is-one-quarter">
                  <TextField
                    label={i18n.reservation.formPage.boatSpaceType}
                    value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
                    readonly={true}
                  />
                </div>
              </div>
            </div>
          )}
        </Loader>
      </div>
    </MainSection>
  )
})

type ErrorMessageProps = {
  error: string
}

const ErrorMessage = React.memo(function ErrorMessage({
  error
}: ErrorMessageProps) {
  const i18n = useTranslation()
  const reservationError = error as ReservationError
  if (reservationError === 'BoatSpaceNotAvailable') {
    return (
      <ul className="has-bullets ml-none">
        {i18n.reservation.errors.failedReservation.type.boatSpaceNotAvailable.map(
          (message, index) => (
            <li key={index}>{message}</li>
          )
        )}
      </ul>
    )
  }

  return (
    <ul className="has-bullets ml-none">
      {i18n.reservation.errors.failedReservation.type.unknown.map(
        (message, index) => (
          <li key={index}>{message}</li>
        )
      )}
    </ul>
  )
})
