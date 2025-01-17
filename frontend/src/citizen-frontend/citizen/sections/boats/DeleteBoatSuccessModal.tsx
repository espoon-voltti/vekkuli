import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { Success } from 'lib-icons'

export type DeleteBoatSuccessModalProps = {
  onClose: () => void
}

export default React.memo(function DeleteBoatSuccessModal({
  onClose
}: DeleteBoatSuccessModalProps) {
  setTimeout(() => onClose(), 3000)
  return (
    <Modal close={onClose} data-testid="delete-boat-success-modal">
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Success />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            Vene poistettu onnistuneesti
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
