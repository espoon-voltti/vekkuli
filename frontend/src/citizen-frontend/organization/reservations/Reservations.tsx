import { Container } from 'lib-components/dom'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'

import Reservation from './Reservation'

export default React.memo(function Reservations({
  reservations
}: {
  reservations: BoatSpaceReservation[]
}) {
  if (!reservations.length) return null

  return (
    <Container isBlock>
      <h3>Paikkavaraukset</h3>
      <div className="reservation-list form-section">
        {reservations.map((reservation) => (
          <Reservation key={reservation.id} reservation={reservation} />
        ))}
      </div>
    </Container>
  )
})
