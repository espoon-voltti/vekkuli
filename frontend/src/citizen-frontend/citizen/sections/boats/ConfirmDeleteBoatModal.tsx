import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Boat } from 'citizen-frontend/shared/types'

export type ConfirmDeleteBoatModalProps = {
  boat: Boat
  onConfirm: () => void
  onCancel: () => void
  isPending: boolean
}

export default React.memo(function ConfirmDeleteBoatModal({
  boat,
  onConfirm,
  onCancel,
  isPending
}: ConfirmDeleteBoatModalProps) {
  const i18n = useTranslation()
  const buttons = [
    {
      label: 'Peruuta',
      action: onCancel
    },
    {
      label: 'Vahvista poisto',
      type: 'danger' as const,
      loading: isPending,
      action: onConfirm
    }
  ]

  return (
    <Modal
      title="Olet poistamassa venettäsi:"
      close={close}
      buttons={buttons}
      data-testid="delete-boat-modal"
    >
      <Columns isMultiline>
        <Column isFull>
          <ul className="no-bullets">
            <li data-testid="boat-name">{boat.name}</li>
            {boat.hasNoRegistrationNumber === false && (
              <li data-testid="boat-registration">{boat.registrationNumber}</li>
            )}
            <li data-testid="boat-identification">
              {boat.otherIdentification}
            </li>
          </ul>
        </Column>
        <Column isFull>
          <p>Oletko varma, että haluat poistaa veneen?</p>
        </Column>
        <Column isFull>
          <p>Poistamista ei voi peruuttaa.</p>
        </Column>
      </Columns>
    </Modal>
  )
})
