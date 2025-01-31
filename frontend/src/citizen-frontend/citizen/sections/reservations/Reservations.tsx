import { Container } from 'lib-components/dom'
import React from 'react'

import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'

import Reservation from './Reservation'

export default React.memo(function Reservations({
  reservations
}: {
  reservations: ExistingBoatSpaceReservation[]
}) {
  if (!reservations.length) return null

  return (
    <Container isBlock data-testid="reservation-list">
      <h3>Paikkavaraukset</h3>
      <div className="reservation-list form-section">
        {reservations.map((reservation) => (
          <Reservation
            key={reservation.id}
            canTerminate
            reservation={reservation}
          />
        ))}
      </div>
    </Container>
  )
})
