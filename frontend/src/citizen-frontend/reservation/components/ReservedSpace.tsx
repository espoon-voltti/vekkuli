import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'

import { RevisedPriceForReservation } from '../RevisedPriceForReservation'

import ReserverPriceInfo from './ReserverPriceInfo'

export default React.memo(function ReservedSpace({
  reservation,
  revisedPriceForReservation
}: {
  reservation: BoatSpaceReservation
  revisedPriceForReservation: RevisedPriceForReservation
}) {
  const i18n = useTranslation()
  const { netPrice, totalPrice, vatValue, boatSpace } = reservation

  const hasStorageType =
    reservation.boatSpace.type === 'Winter' ||
    reservation.boatSpace.type === 'Storage'

  return (
    <>
      <div className="form-section" data-testid="reserved-space">
        <h3 className="header">Varattava paikka</h3>
        <div className="columns">
          <div className="column is-one-quarter">
            <TextField
              label="Satama"
              value={boatSpace.locationName ?? '-'}
              readonly={true}
            />
          </div>
          <div className="column is-one-quarter">
            <TextField
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
              label="Venepaikkatyyppi"
              value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
              readonly={true}
            />
          </div>
          <div className="column is-one-quarter">
            <TextField
              label="Paikan koko"
              value={formatDimensions(boatSpace)}
              readonly={true}
            />
          </div>
        </div>
        <div className="columns">
          <div className="column is-one-quarter">
            {!hasStorageType && (
              <TextField
                label="Varuste"
                value={i18n.boatSpace.amenities[boatSpace.amenity]}
                readonly={true}
              />
            )}
            {hasStorageType && (
              <TextField
                label="Säilytystapa"
                value={
                  reservation.storageType
                    ? i18n.boatSpace.winterStorageType[reservation.storageType]
                    : '-'
                }
                readonly={true}
              />
            )}
          </div>
          <div className="column is-one-quarter">
            <TextField
              label="Varaus voimassa:"
              value={i18n.reservation.validity(
                reservation.endDate,
                revisedPriceForReservation.validity,
                true
              )}
              readonly={true}
              dataTestId="reservation-validity"
            />
          </div>
          <div className="column is-half">
            <TextField
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
        <ReserverPriceInfo
          revisedPriceForReservation={revisedPriceForReservation}
        />
      </div>
    </>
  )
})
