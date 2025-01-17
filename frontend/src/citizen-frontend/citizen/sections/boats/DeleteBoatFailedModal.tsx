import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { Failure } from 'lib-icons'

export type DeleteBoatFailedModalProps = {
  onClose: () => void
}

export default React.memo(function DeleteBoatFailedModal({
  onClose
}: DeleteBoatFailedModalProps) {
  const buttons = [
    {
      label: 'Ok',
      type: 'danger' as const
    }
  ]

  return (
    <Modal
      close={onClose}
      buttons={buttons}
      buttonsCentered
      data-testid="delete-boat-failed-modal"
    >
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Failure />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            Veneen poistamisessa tapahtui virhe. Ota yhteyttä asiakaspalveluun.
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
