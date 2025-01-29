import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { citizenBoatsQuery } from 'citizen-frontend/shared/queries'
import { Boat } from 'citizen-frontend/shared/types'
import { useQueryResult } from 'lib-common/query'

import { citizenActiveReservationsQuery } from '../../citizen/queries'

import BoatComponent from './Boat'
import BoatsNotInReservation from './BoatsNotInReservation'

export default React.memo(function Reservations() {
  const activeReservations = useQueryResult(citizenActiveReservationsQuery())
  const boats = useQueryResult(citizenBoatsQuery())

  return (
    <Container isBlock data-testid="boat-list">
      <h3>Veneet</h3>
      <Loader results={[boats, activeReservations]}>
        {(loadedBoats, reservations) => (
          <>
            <div className="reservation-list form-section no-bottom-border">
              {boatsInActiveReservationsFilter(loadedBoats, reservations).map(
                (boat) => (
                  <BoatComponent key={boat.id} boat={boat} />
                )
              )}
            </div>
            <BoatsNotInReservation
              boats={boatsInActiveReservationsFilter(
                loadedBoats,
                reservations,
                false
              )}
            />
          </>
        )}
      </Loader>
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
