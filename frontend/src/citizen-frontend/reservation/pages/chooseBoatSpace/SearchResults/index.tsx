import React from 'react'

import { PlaceWithSpaces } from 'citizen-frontend/api-types/free-spaces'
import { useTranslation } from 'citizen-frontend/localization'

import { InfoBox } from '../../../components/InfoBox'

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
  const i18n = useTranslation()
  return (
    <div className="block loaded-content" data-testid="boat-space-results">
      <h3>{`${i18n.reservation.searchPage.freeSpaceCount} (${count})`}</h3>
      {!showInfoBox ? null : (
        <InfoBox text={i18n.reservation.searchPage.missingFieldsInfoBox} />
      )}
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
