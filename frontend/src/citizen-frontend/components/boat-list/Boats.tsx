import { Container } from 'lib-components/dom'
import React from 'react'

import { UpdateBoatRequest } from 'citizen-frontend/api-clients/boat'
import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { Boat, BoatId } from 'citizen-frontend/shared/types'
import { MutationDescription } from 'lib-common/query'

import BoatComponent from './Boat'
import BoatsNotInReservation from './BoatsNotInReservation'

type BoatsProps = {
  boats: Boat[]
  activeReservations: ExistingBoatSpaceReservation[]
  deleteMutation?: MutationDescription<BoatId, void>
  updateMutation?: MutationDescription<UpdateBoatRequest, void>
}

export default React.memo(function Boats({
  boats,
  activeReservations,
  deleteMutation,
  updateMutation
}: BoatsProps) {
  return (
    <Container isBlock data-testid="boat-list">
      <h3>Veneet</h3>
      <div className="reservation-list form-section no-bottom-border">
        {boatsInActiveReservationsFilter(boats, activeReservations).map(
          (boat) => (
            <BoatComponent
              key={boat.id}
              boat={boat}
              updateMutation={updateMutation}
            />
          )
        )}
      </div>
      <BoatsNotInReservation
        boats={boatsInActiveReservationsFilter(
          boats,
          activeReservations,
          false
        )}
        deleteMutation={deleteMutation}
        updateMutation={updateMutation}
      />
    </Container>
  )
})

const boatsInActiveReservationsFilter = (
  boats: Boat[],
  activeReservations: ExistingBoatSpaceReservation[],
  active = true
) => {
  const boatIds = new Set(
    activeReservations.map((reservation) => reservation.boat.id)
  )
  return boats.filter((boat) => active === boatIds.has(boat.id))
}
