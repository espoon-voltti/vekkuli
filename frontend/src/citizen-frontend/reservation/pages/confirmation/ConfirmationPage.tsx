import React from 'react'

import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'
import useRouteParams from 'lib-common/useRouteParams'

import { BoatSpaceReservation } from '../../../api-types/reservation'
import StepIndicator from '../../StepIndicator'
import ReservedSpace from '../../components/ReservedSpace'

import { getReservationQuery } from './queries'

export default React.memo(function ConfirmationPage() {
  const { reservationId } = useRouteParams(['reservationId'])

  const reservation = useQueryResult(
    getReservationQuery(parseInt(reservationId))
  )

  return (
    <section className="section">
      <StepIndicator step="confirmation" />
      <div className="container">
        <Content reservation={reservation} />
      </div>
    </section>
  )
})

const Content = React.memo(function Content({
  reservation
}: {
  reservation: Result<BoatSpaceReservation>
}) {
  if (reservation.isLoading) {
    return <div>Loading...</div>
  }

  if (reservation.isFailure) {
    return <div>Error...</div>
  }

  const { boatSpace, netPrice, totalPrice, vatValue } = reservation.value

  return (
    <>
      <h1>Venepaikan varaus onnistui</h1>
      <div className="container">
        <ul className="has-bullets ml-none">
          <li>
            Saat viestin vahvistuksesta myös ilmoittamaasi
            sähköpostiosoitteeseen.
          </li>
          <li>
            Vahvistussähköpostissa on lisätietoa varaamastasi venepaikasta ja
            sataman käytännöistä.
          </li>
          <li>
            Varauksesi on voimassa toistaiseksi ja voit jatkaa sitä seuraavalle
            kaudelle aina tammikuussa.
          </li>
        </ul>
      </div>
      <ReservedSpace
        boatSpace={boatSpace}
        reservation={reservation.value}
        price={{ netPrice, totalPrice, vatValue }}
      />
    </>
  )
})
