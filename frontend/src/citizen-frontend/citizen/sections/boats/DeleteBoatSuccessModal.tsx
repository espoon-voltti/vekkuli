import { Column, Columns, ScreenReaderOnly } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization/state'
import { Success } from 'lib-icons'

export type DeleteBoatSuccessModalProps = {
  onClose: () => void
}

export default React.memo(function DeleteBoatSuccessModal({
  onClose
}: DeleteBoatSuccessModalProps) {
  const i18n = useTranslation()
  setTimeout(() => onClose(), 3000)
  return (
    <Modal close={onClose} data-testid="delete-boat-success-modal">
      <ScreenReaderOnly>{i18n.boat.deleteSuccess}</ScreenReaderOnly>
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Success />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            {i18n.boat.deleteSuccess}
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
