import Accordion from 'lib-components/accordion/Accordion'
import { Container } from 'lib-components/dom'
import React from 'react'

import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'

import Reservation from './Reservation'

export default React.memo(function ExpiredReservations({
  reservations
}: {
  reservations: ExistingBoatSpaceReservation[]
}) {
  const i18n = useTranslation()
  if (!reservations.length) return null

  return (
    <Container isBlock>
      <h3>{i18n.citizenPage.expired}</h3>
      <div
        className="reservation-list form-section"
        data-testid="expired-reservation-list"
      >
        <Accordion title={i18n.citizenPage.expiredReservations}>
          {reservations.map((reservation) => (
            <Reservation key={reservation.id} reservation={reservation} />
          ))}
        </Accordion>
      </div>
    </Container>
  )
})
