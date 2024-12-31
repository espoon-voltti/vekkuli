import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'

export default React.memo(function ReservedSpace({
  reservation
}: {
  reservation: BoatSpaceReservation
}) {
  const i18n = useTranslation()
  const { netPrice, totalPrice, vatValue, boatSpace } = reservation
  return (
    <div className="form-section">
      <h3 className="header">Varattava paikka</h3>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="space-location"
            label="Satama"
            value={boatSpace.locationName ?? '-'}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="place-number"
            label="Paikka"
            value={formatPlaceIdentifier(
              boatSpace.section,
              boatSpace.placeNumber
            )}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="space-type"
            label="Venepaikkatyyppi"
            value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="space-size"
            label="Paikan koko"
            value={formatDimensions(boatSpace)}
            readonly={true}
          />
        </div>
      </div>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="amenity"
            label="Varuste"
            value={i18n.boatSpace.amenities[boatSpace.amenity]}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="amenity"
            label="Varaus voimassa:"
            value={i18n.reservation.validity(
              reservation.endDate,
              reservation.validity,
              boatSpace.type
            )}
            readonly={true}
          />
        </div>
        <div className="column is-half">
          <TextField
            id="price"
            label="Hinta"
            value={[
              i18n.reservation.prices.netPrice(netPrice),
              i18n.reservation.prices.vatValue(vatValue),
              i18n.reservation.prices.totalPrice(totalPrice)
            ]}
            readonly={true}
          />
        </div>
      </div>
    </div>
  )
})
