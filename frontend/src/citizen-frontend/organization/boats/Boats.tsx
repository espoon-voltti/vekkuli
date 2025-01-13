import { Container } from 'lib-components/dom'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'

import { Boat } from 'citizen-frontend/shared/types'

import BoatComponent from './Boat'
import BoatsNotInReservation from './BoatsNotInReservation'

export default React.memo(function Reservations({
  reservations,
  boats
}: {
  reservations: BoatSpaceReservation[]
  boats: Boat[]
}) {
  return (
    <Container isBlock>
      <h3>Veneet</h3>
      <>
        <div className="reservation-list form-section no-bottom-border">
          {boatsInActiveReservationsFilter(boats, reservations).map((boat) => (
            <BoatComponent key={boat.id} boat={boat} />
          ))}
        </div>
        <BoatsNotInReservation
          boats={boatsInActiveReservationsFilter(boats, reservations, false)}
        />
      </>
    </Container>
  )
})

const boatsInActiveReservationsFilter = (
  boats: Boat[],
  activeReservations: BoatSpaceReservation[],
  active = true
) => {
  const boatIds = new Set(
    activeReservations.map((reservation) => reservation.boat.id)
  )
  return boats.filter((boat) => active === boatIds.has(boat.id))
}
