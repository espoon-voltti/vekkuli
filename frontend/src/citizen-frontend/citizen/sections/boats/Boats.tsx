import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React from 'react'

import { useQueryResult } from 'lib-common/query'

import { citizenBoatsQuery } from '../../../shared/queries'

import Boat from './Boat'

export default React.memo(function Reservations() {
  const boats = useQueryResult(citizenBoatsQuery())

  return (
    <Container isBlock>
      <h3>Veneet</h3>
      <div className="reservation-list form-section">
        <Loader result={boats}>
          {(loadedBoats) =>
            loadedBoats.map((boat) => <Boat key={boat.id} boat={boat} />)
          }
        </Loader>
      </div>
    </Container>
  )
})
