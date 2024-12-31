import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import { Result } from 'lib-common/api'
import { useMutationResult } from 'lib-common/query'

import { terminateReservationMutation } from './queries'

export type TerminateModalProps = {
  close: () => void
  reservation: BoatSpaceReservation
  onTermination: (mutation: Promise<Result<void>>) => void
}

export default React.memo(function TerminateModal({
  close,
  reservation,
  onTermination
}: TerminateModalProps) {
  const i18n = useTranslation()
  const { mutateAsync: terminateReservation, isPending } = useMutationResult(
    terminateReservationMutation
  )
  const buttons = [
    {
      label: 'Peruuta'
    },
    {
      label: 'Irtisano venepaikka',
      type: 'danger' as const,
      loading: isPending,
      action: () => {
        return onTermination(terminateReservation(reservation.id))
      }
    }
  ]

  return (
    <Modal
      title="Olet irtisanomassa venepaikkaa:"
      close={close}
      buttons={buttons}
    >
      <Columns isMultiline>
        <Column isFull>
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
            <li>{i18n.boatSpace.amenities[reservation.boatSpace.amenity]}</li>
          </ul>
        </Column>
        <Column isFull>
          <p>
            Huomioi, että sinun on siirrettävä veneesi välittömästi pois
            venepaikalta kun olet irtisanonut paikan.
          </p>
        </Column>
        <Column isFull>
          <p>Espoon kaupunki ei myönnä hyvitystä maksetusta venepaikasta.</p>
        </Column>
      </Columns>
    </Modal>
  )
})
