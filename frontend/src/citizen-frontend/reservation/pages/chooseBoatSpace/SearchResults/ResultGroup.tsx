import React from 'react'

import { Place, Space } from 'citizen-frontend/api-types/free-spaces'

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

  return (
    <div className="block">
      <div className="mb-m">
        <h3 className="subtitle harbor-header mb-s">{place.name}</h3>
      </div>
      <table className="table search-results-table is-striped is-hoverable is-fullwidth">
        <thead>
          <tr>
            <th>Paikan koko</th>
            <th>Varuste</th>
            <th>Hinta/Kausi</th>
            <th>Paikka</th>
          </tr>
        </thead>
        <tbody>
          {limitedSpaces.map((space) => (
            <ResultRow
              onReserveSpace={onReserveSpace}
              space={space}
              key={`result-row-${space.id}`}
            />
          ))}
        </tbody>
      </table>
      <ShowMore
        minResultCount={minResultsCount}
        resultCount={spacesCount}
        showMoreState={showMore}
        setShowMoreState={setShowMore}
      />
    </div>
  )
})
