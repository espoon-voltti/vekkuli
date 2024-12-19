import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React from 'react'

import { useQueryResult } from 'lib-common/query'

import { citizenActiveReservationsQuery } from '../../queries'

import Reservation from './Reservation'

export default React.memo(function Reservations() {
  const activeReservations = useQueryResult(citizenActiveReservationsQuery())

  return (
    <Container isBlock>
      <h3>Paikkavaraukset</h3>
      <div className="reservation-list form-section">
        <Loader result={activeReservations}>
          {(loadedReservations) =>
            loadedReservations.map((reservation) => (
              <Reservation key={reservation.id} reservation={reservation} />
            ))
          }
        </Loader>
      </div>
    </Container>
  )
})
