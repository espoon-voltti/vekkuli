import Accordion from 'lib-components/accordion/Accordion'
import { Container } from 'lib-components/dom'
import React from 'react'

import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'

import Reservation from './Reservation'

export default React.memo(function ExpiredReservations({
  reservations
}: {
  reservations: ExistingBoatSpaceReservation[]
}) {
  if (!reservations.length) return null

  return (
    <Container isBlock>
      <h3>Päättyneet</h3>
      <div
        className="reservation-list form-section"
        data-testid="expired-reservation-list"
      >
        <Accordion title="Päättyneet varaukset">
          {reservations.map((reservation) => (
            <Reservation
              key={reservation.id}
              reservation={reservation}
              canTerminate={false}
            />
          ))}
        </Accordion>
      </div>
    </Container>
  )
})
