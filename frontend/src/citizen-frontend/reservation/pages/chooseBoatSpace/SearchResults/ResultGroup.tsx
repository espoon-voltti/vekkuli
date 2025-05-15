import React from 'react'

import { Place, Space } from 'citizen-frontend/api-types/free-spaces'
import { useTranslation } from 'citizen-frontend/localization'

import { BridgeHeightWarningBox } from './BridgeHeightWarningBox'
import { ResultRow } from './ResultRow'
import { ShowMore } from './ShowMore'

interface ResultGroupProps {
  place: Place
  spaces: Space[]
  onReserveSpace: (spaceId: number) => void
}

export const ResultGroup = React.memo(function ResultGroup({
  place,
  spaces,
  onReserveSpace
}: ResultGroupProps) {
  const spacesCount = spaces.length
  const minResultsCount = 5
  const [showMore, setShowMore] = React.useState(false)
  const limitedSpaces = showMore ? spaces : spaces.slice(0, minResultsCount)
  const i18n = useTranslation()

  return (
    <div className="block">
      <div className="mb-m">
        <h3 className="subtitle harbor-header mb-s">
          {i18n.boatSpace.harbors[place.id]}
        </h3>
        <div className="block mb-s">{place.address}</div>
        <BridgeHeightWarningBox placeId={place.id} />
      </div>
      <div className="search-results-table-container">
        <table className="table search-results-table is-striped is-hoverable is-narrow">
          <thead>
            <tr>
              <th>{i18n.reservation.searchPage.size}</th>
              <th>{i18n.reservation.searchPage.amenityLabel}</th>
              <th>{i18n.reservation.searchPage.price}</th>
              <th>{i18n.reservation.searchPage.place}</th>
            </tr>
          </thead>
          <tbody>
            {limitedSpaces.map((space) => (
              <ResultRow
                onReserveSpace={onReserveSpace}
                space={space}
                placeName={i18n.boatSpace.harbors[place.id]}
                key={`result-row-${space.id}`}
              />
            ))}
          </tbody>
        </table>
      </div>
      <ShowMore
        minResultCount={minResultsCount}
        resultCount={spacesCount}
        showMoreState={showMore}
        setShowMoreState={setShowMore}
        ariaLabel={i18n.boatSpace.harbors[place.id]}
      />
    </div>
  )
})
