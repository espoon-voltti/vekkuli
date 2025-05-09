import { Loader } from 'lib-components/Loader'
import { MainSection } from 'lib-components/dom'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'
import useRouteParams from 'lib-common/useRouteParams'

import { getReservationInfoForReservation } from '../../ReservationInfoForReservation'
import StepIndicator from '../../StepIndicator'
import ReservedSpace from '../../components/ReservedSpace'

import { getReservationQuery } from './queries'

export default React.memo(function ConfirmationPage() {
  const i18n = useTranslation()
  const { reservationId } = useRouteParams(['reservationId'])

  const reservation = useQueryResult(
    getReservationQuery(parseInt(reservationId))
  )

  return (
    <MainSection
      dataTestId="confirmation-page"
      ariaLabel={i18n.reservation.steps.confirmation}
    >
      <StepIndicator step="confirmation" />
      <div className="container">
        <Content reservation={reservation} />
      </div>
    </MainSection>
  )
})

const Content = React.memo(function Content({
  reservation
}: {
  reservation: Result<BoatSpaceReservation>
}) {
  const i18n = useTranslation()
  return (
    <Loader results={[reservation]}>
      {(loadedReservation) => {
        const reservationPriceInfo = getReservationInfoForReservation(
          loadedReservation,
          loadedReservation.reservationInfo
        )
        const isIndefinite =
          loadedReservation.reservationInfo.validity === 'Indefinite'
        return (
          <>
            <h2 className="h1">{i18n.reservation.confirmationPage.header}</h2>
            <div className="container">
              <ul className="has-bullets ml-none">
                <li>{i18n.reservation.confirmationPage.emailInfo}</li>
                <li>
                  {isIndefinite
                    ? i18n.reservation.confirmationPage.indefiniteInfo
                    : i18n.reservation.confirmationPage.fixedInfo}
                </li>
              </ul>
            </div>
            <ReservedSpace
              reservation={loadedReservation}
              reservationInfoForReservation={reservationPriceInfo}
              showPriceInfo={false}
            />
          </>
        )
      }}
    </Loader>
  )
})
