import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'

import { ReservationInfoForReservation } from '../ReservationInfoForReservation'

import ReserverPriceInfo from './ReserverPriceInfo'

export default React.memo(function ReservedSpace({
  reservation,
  reservationInfoForReservation
}: {
  reservation: BoatSpaceReservation
  reservationInfoForReservation: ReservationInfoForReservation
}) {
  const i18n = useTranslation()
  const { netPrice, totalPrice, vatValue, boatSpace } = reservation

  const hasStorageType =
    reservation.boatSpace.type === 'Winter' ||
    reservation.boatSpace.type === 'Storage'

  return (
    <>
      <div className="form-section" data-testid="reserved-space">
        <h3 className="header">
          {i18n.reservation.formPage.boatSpaceInformation}
        </h3>
        <div className="columns">
          <div className="column is-one-quarter">
            <TextField
              label={i18n.reservation.formPage.harbor}
              value={boatSpace.locationName ?? '-'}
              readonly={true}
            />
          </div>
          <div className="column is-one-quarter">
            <TextField
              label={i18n.reservation.formPage.place}
              value={formatPlaceIdentifier(
                boatSpace.section,
                boatSpace.placeNumber
              )}
              readonly={true}
            />
          </div>
          <div className="column is-one-quarter">
            <TextField
              label={i18n.reservation.formPage.boatSpaceType}
              value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
              readonly={true}
            />
          </div>
          <div className="column is-one-quarter">
            <TextField
              label={i18n.reservation.formPage.boatSpaceDimensions}
              value={formatDimensions(boatSpace)}
              readonly={true}
            />
          </div>
        </div>
        <div className="columns">
          <div className="column is-one-quarter">
            {!hasStorageType && (
              <TextField
                label={i18n.reservation.formPage.boatSpaceAmenity}
                value={i18n.boatSpace.amenities[boatSpace.amenity]}
                readonly={true}
              />
            )}
            {hasStorageType && (
              <TextField
                label={i18n.reservation.formPage.storageType}
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
              label={i18n.reservation.formPage.reservationValidity}
              value={i18n.reservation.validity(
                reservationInfoForReservation.endDate,
                reservationInfoForReservation.validity,
                true
              )}
              readonly={true}
              dataTestId="reservation-validity"
            />
          </div>
          <div className="column is-half">
            <TextField
              label={i18n.reservation.formPage.price}
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
          reservationInfoForReservation={reservationInfoForReservation}
        />
      </div>
    </>
  )
})
