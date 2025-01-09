import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BlueInfoCircle } from 'lib-icons'

export const InfoBox = React.memo(function InfoBox() {
  const i18n = useTranslation()
  return (
    <div
      id="empty-dimensions-warning"
      className="reservation-info column is-four-fifths"
    >
      <div className="column is-narrow">
        <span className="icon">
          <BlueInfoCircle />
        </span>
      </div>
      <p className="column">
        {i18n.reservation.searchPage.missingFieldsInfoBox}
      </p>
    </div>
  )
})
