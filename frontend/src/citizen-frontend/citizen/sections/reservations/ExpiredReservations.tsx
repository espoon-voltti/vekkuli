import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React from 'react'

import { useQueryResult } from 'lib-common/query'

import { citizenExpiredReservationsQuery } from '../../queries'

import Reservation from './Reservation'

export default React.memo(function Reservations() {
  const expiredReservations = useQueryResult(citizenExpiredReservationsQuery())

  return (
    <Container isBlock>
      <h3>Päättyneet</h3>
      <div className="reservation-list form-section">
        <Loader results={[expiredReservations]}>
          {(loadedReservations) =>
            loadedReservations.map((reservation) => (
              <Reservation
                key={reservation.id}
                reservation={reservation}
                canTerminate={false}
              />
            ))
          }
        </Loader>
      </div>
    </Container>
  )
})
