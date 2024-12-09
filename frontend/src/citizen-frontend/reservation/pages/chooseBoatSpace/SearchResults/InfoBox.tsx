import React from 'react'

import { InfoCircle } from '../../../../../lib-icons'
import { useTranslation } from '../../../../localization'

export const InfoBox = React.memo(function InfoBox() {
  const i18n = useTranslation()
  return (
    <div
      id="empty-dimensions-warning"
      className="reservation-info column is-four-fifths"
    >
      <div className="column is-narrow">
        <span className="icon">
          <InfoCircle />
        </span>
      </div>
      <p className="column">
        {i18n.reservation.searchPage.missingFieldsInfoBox}
      </p>
    </div>
  )
})
