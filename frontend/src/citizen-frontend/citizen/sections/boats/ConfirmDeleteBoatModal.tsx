import { Column, Columns, ScreenReaderOnly } from 'lib-components/dom'
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
      label: i18n.common.cancel,
      action: onCancel
    },
    {
      label: i18n.boat.delete,
      type: 'danger' as const,
      loading: isPending,
      action: onConfirm
    }
  ]

  return (
    <Modal close={close} buttons={buttons} data-testid="delete-boat-modal">
      <ScreenReaderOnly>{i18n.boat.confirmDelete(boat.name)}</ScreenReaderOnly>
      <Columns isMultiline>
        <Column isFull>
          <p>{i18n.boat.confirmDelete(boat.name)}</p>
        </Column>
      </Columns>
    </Modal>
  )
})
