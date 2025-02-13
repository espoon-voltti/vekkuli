import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import { ReservationId } from 'citizen-frontend/shared/types'
import { MutationDescription, useMutationResult } from 'lib-common/query'

export type TerminateModalProps = {
  onCancel: () => void
  onFailure: () => void
  onSuccess: () => void
  reservation: ExistingBoatSpaceReservation
  terminateMutation: MutationDescription<ReservationId, void>
}

export default React.memo(function TerminateModal({
  reservation,
  onCancel,
  onFailure,
  onSuccess,
  terminateMutation
}: TerminateModalProps) {
  const i18n = useTranslation()
  const { mutateAsync: terminateReservation, isPending } =
    useMutationResult(terminateMutation)
  const buttons = [
    {
      label: i18n.common.cancel,
      action: onCancel
    },
    {
      label: i18n.citizenPage.reservation.modal.termination.confirm,
      type: 'danger' as const,
      loading: isPending,
      action: () => {
        terminateReservation(reservation.id)
          .then((result) => {
            if (result.isSuccess) {
              onSuccess()
            } else if (result.isFailure) {
              onFailure()
            }
          })
          .catch(onFailure)
      }
    }
  ]

  return (
    <Modal
      title={i18n.citizenPage.reservation.modal.termination.title}
      close={close}
      buttons={buttons}
      data-testid="terminate-reservation-modal"
    >
      <Columns isMultiline>
        <Column isFull>
          <ul className="no-bullets">
            <li data-testid="place-identifier">
              {formatPlaceIdentifier(
                reservation.boatSpace.section,
                reservation.boatSpace.placeNumber,
                reservation.boatSpace.locationName
              )}
            </li>
            <li data-testid="boat-space">
              {formatDimensions({
                width: reservation.boatSpace.width,
                length: reservation.boatSpace.length
              })}
            </li>
            <li data-testid="amenity">
              {i18n.boatSpace.amenities[reservation.boatSpace.amenity]}
            </li>
          </ul>
        </Column>
        <Column isFull>
          <p>
            {i18n.citizenPage.reservation.modal.termination.moveBoatImmediately}
          </p>
        </Column>
        <Column isFull>
          <p>
            {i18n.citizenPage.reservation.modal.termination.notEntitledToRefund}
          </p>
        </Column>
      </Columns>
    </Modal>
  )
})
