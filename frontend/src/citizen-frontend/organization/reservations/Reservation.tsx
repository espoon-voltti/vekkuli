import { Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatNumber,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import TrailerInformation from './TrailerInformation'

export default React.memo(function Reservation({
  reservation
}: {
  reservation: BoatSpaceReservation
}) {
  const i18n = useTranslation()

  const { boatSpace } = reservation

  return (
    <>
      <div className="reservation-card">
        <Columns isVCentered>
          <Column isNarrow>
            <h4>
              {i18n.boatSpace.boatSpaceType[reservation.boatSpace.type].label}:{' '}
              {formatPlaceIdentifier(
                boatSpace.section,
                boatSpace.placeNumber,
                boatSpace.locationName
              )}
            </h4>
          </Column>
        </Columns>
        <Columns>
          <Column>
            <TextField
              label="Satama"
              value={boatSpace.locationName || undefined}
              readonly={true}
            />
            <NumberField
              label="Leveys (m)"
              value={formatNumber(boatSpace.width)}
              readonly={true}
            />
            <TextField
              label="Varaus tehty"
              value={reservation.created.format()}
              readonly={true}
            />
          </Column>
          <Column>
            <TextField
              label="Paikka"
              value={formatPlaceIdentifier(
                reservation.boatSpace.section,
                reservation.boatSpace.placeNumber
              )}
              readonly={true}
            />
            <NumberField
              label="Pituus (m)"
              value={formatNumber(boatSpace.length)}
              readonly={true}
            />
            <TextField
              label="Varaus voimassa"
              value={i18n.reservation.validity(
                reservation.endDate,
                reservation.validity,
                boatSpace.type
              )}
              readonly={true}
            />
          </Column>
          <Column>
            <TextField
              label="Paikan tyyppi"
              value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
              readonly={true}
            />
            <TextField
              label="Hinta"
              value={i18n.reservation.totalPrice(
                reservation.totalPrice,
                reservation.vatValue
              )}
              readonly={true}
            />
            <TextField
              label="Paikalla oleva vene"
              value={reservation.boat.name}
              readonly={true}
            />
          </Column>
          <Column>
            {boatSpace.type !== 'Winter' && (
              <TextField
                label="Varuste"
                value={i18n.boatSpace.amenities[reservation.boatSpace.amenity]}
                readonly={true}
              />
            )}
            {boatSpace.type === 'Winter' && (
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
            <TextField
              label="Maksun tila"
              value={i18n.reservation.paymentState(reservation.paymentDate)}
              readonly={true}
            />
          </Column>
        </Columns>
        {reservation.trailer && (
          <TrailerInformation trailer={reservation.trailer} />
        )}
      </div>
    </>
  )
})
