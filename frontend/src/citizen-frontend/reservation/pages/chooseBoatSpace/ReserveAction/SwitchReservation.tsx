import { Button, Column, Columns } from 'lib-components/dom'
import React from 'react'
import { useNavigate } from 'react-router'

import { SwitchableReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import { useMutation } from 'lib-common/query'

import { starSwitchSpaceMutation } from '../queries'

export type SwitchReservationProps = {
  reservation: SwitchableReservation
  reserveSpaceId: number
}

export default React.memo(function SwitchReservation({
  reservation,
  reserveSpaceId
}: SwitchReservationProps) {
  const i18n = useTranslation()
  const { mutateAsync: switchPlace, isPending } = useMutation(
    starSwitchSpaceMutation
  )
  const navigate = useNavigate()
  const onSwitch = () => {
    // eslint-disable-next-line no-console
    console.log(`switching ${reservation.id} to ${reserveSpaceId}`)
    switchPlace({
      reservationId: reservation.id,
      spaceId: reserveSpaceId
    })
      .then((response) => {
        console.info('switch place response', response)
        return navigate('/kuntalainen/venepaikka/vaihda')
      })
      .catch((error) => {
        const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
        console.error(errorCode)
        //const errorType = mapErrorCode(errorCode)
        //setReserveError(errorType)
      })
  }

  return (
    <Column isFull key={reservation.id}>
      <Columns>
        <Column isHalf>
          <ul className="no-bullets">
            <li>
              {formatPlaceIdentifier(
                reservation.boatSpace.section,
                reservation.boatSpace.placeNumber,
                reservation.boatSpace.locationName
              )}
            </li>
            <li>
              {formatDimensions({
                width: reservation.boatSpace.width,
                length: reservation.boatSpace.length
              })}
            </li>
            {reservation.boatSpace.amenity && (
              <li>{i18n.boatSpace.amenities[reservation.boatSpace.amenity]}</li>
            )}
          </ul>
        </Column>
        <Column isNarrow>
          <Button type="primary" action={onSwitch} loading={isPending}>
            {i18n.reservation.searchPage.modal.switchCurrentPlace}
          </Button>
        </Column>
      </Columns>
    </Column>
  )
})
