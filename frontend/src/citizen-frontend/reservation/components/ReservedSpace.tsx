import TextField from 'lib-components/form/TextField'
import React from 'react'

import { useTranslation } from '../../localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from '../../shared/formatters'
import { BoatSpace } from '../../shared/types'

export default React.memo(function ReserveredSpace({
  boatSpace,
  price: { totalPrice, vatValue, netPrice }
}: {
  boatSpace: BoatSpace
  price: {
    totalPrice: string
    vatValue: string
    netPrice: string
  }
}) {
  const i18n = useTranslation()

  return (
    <div className="form-section">
      <h3 className="header">Varaaja</h3>
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
            value={boatSpace.type}
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
            value={boatSpace.amenity}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="amenity"
            label="Varaus voimassa:"
            value="01.04.2024 - 31.01.2025"
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
