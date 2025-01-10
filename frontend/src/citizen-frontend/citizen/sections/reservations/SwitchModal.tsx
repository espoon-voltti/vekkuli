import { Button, Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import { ModalButton } from 'lib-components/modal/ModalButtons'
import ModalTitle from 'lib-components/modal/ModalTitle'
import React from 'react'

import { PlaceWithSpaces } from 'citizen-frontend/api-types/free-spaces'
import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import { reserveSpaceMutation } from 'citizen-frontend/reservation/pages/chooseBoatSpace/queries'
import { Reservation } from 'citizen-frontend/reservation/state'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'

export type SwitchModalProps = {
  close: () => void
  currentSpace: PlaceWithSpaces
  reservations: Reservation[]
  onSwitch: (spaceId: number, reservationId: number) => void
  onReserveSpace: (spaceId: number) => void
}

export default React.memo(function SwitchModal({
  close,
  currentSpace,
  reservations,
  onSwitch,
  onReserveSpace
}: SwitchModalProps) {
  const i18n = useTranslation()
  const buttons: ModalButton[] = [
    {
      label: 'Peruuta'
    }
  ]
  if (reservations.length === 1)
    buttons.push({
      label: 'Varaan toisen paikan',
      type: 'primary',
      loading: false,
      action: () => {
        return onReserveSpace(currentSpace.place.id)
      }
    })

  return (
    <Modal title="Olet varaamassa venepaikkaa:" close={close} buttons={buttons}>
      <Columns isMultiline>
        <Column isFull>
          <ul className="no-bullets">
            <li>
              {formatPlaceIdentifier(
                currentSpace.spaces[0].identifier,
                currentSpace.place.id,
                currentSpace.place.name
              )}
            </li>
            <li>
              {formatDimensions({
                width: currentSpace.spaces[0].size.width,
                length: currentSpace.spaces[0].size.length
              })}
            </li>
            <li>
              {/* {i18n.boatSpace.amenities[placeWithSpaces.spaces[0].amenity]} */}
            </li>
          </ul>
        </Column>
        <Column noBottomPadding>
          <ModalTitle
            title={
              reservations.length > 1
                ? 'Sinulla on jo kaksi venepaikkaa. Et voi varata uutta paikkaa, mutta voit vaihtaa nykyisen paikkasi.'
                : 'Nykyinen paikkasi'
            }
          />
        </Column>
        {reservations.map((reservation) => (
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
                  <li>
                    {i18n.boatSpace.amenities[reservation.boatSpace.amenity]}
                  </li>
                </ul>
              </Column>
              <Column isNarrow>
                <Button
                  type="primary"
                  action={() => onSwitch(currentSpace.place.id, reservation.id)}
                >
                  Vaihdan nykyisen paikan
                </Button>
              </Column>
            </Columns>
          </Column>
        ))}
      </Columns>
    </Modal>
  )
})
