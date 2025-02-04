import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'

export default React.memo(function ReservationSeasons() {
  const i18n = useTranslation()
  return (
    <div className="mb-xl">
      <div className="container is-highlight">
        <h2 className="has-text-weight-semibold">
          {i18n.reservation.searchPage.infoText.title}
        </h2>
        <h3 className="label">
          {i18n.reservation.searchPage.infoText.periods.newReservations}
        </h3>
        <h3 className="label">
          {i18n.reservation.searchPage.infoText.periods.trailerReservations}
        </h3>
        <h3 className="label">
          {i18n.reservation.searchPage.infoText.periods.winter}
        </h3>
        <h3 className="label">
          {i18n.reservation.searchPage.infoText.periods.storage}
        </h3>
      </div>
    </div>
  )
})
