import { ModalButton } from 'lib-components/modal/ModalButtons'
import React, { useEffect } from 'react'

import { CanReserveReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import Modal from 'lib-components/modal/Modal'
import { Column, Columns } from 'lib-components/dom'
import SwitchReservation from './SwitchReservation'

export type SwitchModalProps = {
  canReserveResult: CanReserveReservation
  reserveSpace: () => void
}

export default React.memo(function SwitchModal({
  canReserveResult,
  reserveSpace
}: SwitchModalProps) {
  const i18n = useTranslation()
  const showModal =
    canReserveResult.status !== 'CanReserve' ||
    canReserveResult.switchableReservations.length > 0
  const buttons: ModalButton[] = [
    {
      label: i18n.common.cancel
    }
  ]

  if (canReserveResult.status === 'CanReserve')
    buttons.push({
      label: i18n.reservation.searchPage.modal.reserveAnotherPlace,
      type: 'primary',
      action: () => {
        return reserveSpace()
      }
    })

  useEffect(() => {
    if (!showModal) {
      reserveSpace()
    }
  }, [canReserveResult, reserveSpace, showModal])

  return (
    showModal && (

      <Modal
        title={i18n.reservation.searchPage.modal.reservingBoatSpace}
        close={close}
        buttons={buttons}
      >
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
            </ul>
          </Column>
          {canReserveResult.switchableReservations.map((reservation) => (
            <SwitchReservation
              key={reservation.id}
              reservation={reservation}
              reserveSpaceId={reserveSpace}
            />
          ))}
        </Columns>
      </Modal>
    )*/
  )
})
