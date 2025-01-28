import { Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import TrailerInformation from 'citizen-frontend/components/trailer/TrailerInformation'
import { useTranslation } from 'citizen-frontend/localization'
import { formatPlaceIdentifier } from 'citizen-frontend/shared/formatters'

import { updateOrganizationTrailerMutation } from '../queries'

export default React.memo(function Reservation({
  reservation
}: {
  reservation: BoatSpaceReservation
}) {
  const i18n = useTranslation()

  const { boatSpace } = reservation

  return (
    <>
      <div className="reservation-card" data-testid="reservation-list-card">
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
              value={boatSpace.width}
              readonly={true}
              precision={2}
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
              value={boatSpace.length}
              readonly={true}
              precision={2}
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
          <TrailerInformation
            trailer={reservation.trailer}
            updateMutation={updateOrganizationTrailerMutation}
          />
        )}
      </div>
    </>
  )
})
