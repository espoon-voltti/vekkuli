import React from 'react'

import { ChevronDown, ChevronUp } from 'lib-icons'

interface ShowMoreProps {
  minResultCount: number
  resultCount: number
  showMoreState: boolean
  setShowMoreState: (showMore: boolean) => void
}

export const ShowMore = React.memo(function ShowMore({
  minResultCount,
  resultCount,
  showMoreState,
  setShowMoreState
}: ShowMoreProps) {
  if (resultCount <= minResultCount) {
    return null
  }

  const text = showMoreState ? 'Näytä vähemmän' : 'Näytä lisää'
  const icon = showMoreState ? ChevronUp : ChevronDown
  return (
    <span>
      <a
        onClick={() => setShowMoreState(!showMoreState)}
        className="is-icon-link"
      >
        <span className="icon is-small">{icon()}</span>
        <span>{text}</span>
      </a>
    </span>
  )
})
