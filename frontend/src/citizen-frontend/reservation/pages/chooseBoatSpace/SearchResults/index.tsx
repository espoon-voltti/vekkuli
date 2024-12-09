import React from 'react'

import { PlaceWithSpaces } from '../../../../api-types/free-spaces'

import { InfoBox } from './InfoBox'
import { ResultGroup } from './ResultGroup'

export default React.memo(function SearchResults({
  placesWithSpaces,
  count,
  showInfoBox,
  onReserveSpace
}: {
  placesWithSpaces: PlaceWithSpaces[]
  count: number
  showInfoBox: boolean
  onReserveSpace: (spaceId: number) => void
}) {
  return (
    <div className="block loaded-content">
      <h3>
        <span>Hakuehtoihin sopivat vapaat paikat</span> <span>({count})</span>
      </h3>
      {!showInfoBox ? null : <InfoBox />}
      {placesWithSpaces.map((placeWithSpaces) => (
        <ResultGroup
          place={placeWithSpaces.place}
          spaces={placeWithSpaces.spaces}
          key={`result-group-${placeWithSpaces.place.id}`}
          onReserveSpace={onReserveSpace}
        />
      ))}
    </div>
  )
})
