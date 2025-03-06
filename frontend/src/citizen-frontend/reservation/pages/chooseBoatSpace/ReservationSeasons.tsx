import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import {Link} from "react-router";
import {MapIcon, OpenInNew} from "../../../../lib-icons";

export default React.memo(function ReservationSeasons() {
  const i18n = useTranslation()

  const MapsLink = () => (
      <div className="is-primary-color ">
        <Link to={i18n.header.harborsInfoLink} className="link open-in-new-link" aria-label={i18n.header.openInANewWindow} target="_blank">
          <MapIcon />
          <span>{i18n.header.mapsLink}</span>
          <OpenInNew />
        </Link>
      </div>
  )

  return (
    <div className="mb-xl instructions-container">
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
          {i18n.reservation.searchPage.infoText.periods.storage1}
        </h3>
        <h3 className="label">
          {i18n.reservation.searchPage.infoText.periods.storage2}
        </h3>
      </div>

      <MapsLink />
    </div>
  )
})
