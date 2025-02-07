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
  return (
    <Loader results={[reservation]}>
      {(loadedReservation) => {
        const reservationPriceInfo = getReservationInfoForReservation(
          loadedReservation,
          loadedReservation.reservationInfo
        )
        return (
          <>
            <h2 className="h1">Venepaikan varaus onnistui</h2>
            <div className="container">
              <ul className="has-bullets ml-none">
                <li>
                  Saat viestin vahvistuksesta myös ilmoittamaasi
                  sähköpostiosoitteeseen.
                </li>
                <li>
                  Vahvistussähköpostissa on lisätietoa varaamastasi venepaikasta
                  ja sataman käytännöistä.
                </li>
                <li>
                  Varauksesi on voimassa toistaiseksi ja voit jatkaa sitä
                  seuraavalle kaudelle aina tammikuussa.
                </li>
              </ul>
            </div>
            <ReservedSpace
              reservation={loadedReservation}
              reservationInfoForReservation={reservationPriceInfo}
            />
          </>
        )
      }}
    </Loader>
  )
})
