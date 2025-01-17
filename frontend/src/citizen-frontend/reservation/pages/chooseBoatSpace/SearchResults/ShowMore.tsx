import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ChevronDown, ChevronUp } from 'lib-icons'

interface ShowMoreProps {
  minResultCount: number
  resultCount: number
  showMoreState: boolean
  setShowMoreState: (showMore: boolean) => void
  ariaLabel?: string
}

export const ShowMore = React.memo(function ShowMore({
  minResultCount,
  resultCount,
  showMoreState,
  setShowMoreState,
  ariaLabel
}: ShowMoreProps) {
  const i18n = useTranslation()
  if (resultCount <= minResultCount) {
    return null
  }
  const text = showMoreState ? i18n.common.showLess : i18n.common.showMore
  const icon = showMoreState ? ChevronUp : ChevronDown
  return (
    <span>
      <button
        onClick={() => setShowMoreState(!showMoreState)}
        className="is-icon-link"
        aria-label={ariaLabel ? `${text}: ${ariaLabel}` : text}
      >
        <span className="icon is-small">{icon()}</span>
        <span>{text}</span>
      </button>
    </span>
  )
})
