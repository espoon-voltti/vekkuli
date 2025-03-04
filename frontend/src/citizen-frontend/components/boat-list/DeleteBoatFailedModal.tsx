import { Column, Columns, ScreenReaderOnly } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization/state'
import { Failure } from 'lib-icons'

export type DeleteBoatFailedModalProps = {
  onClose: () => void
}

export default React.memo(function DeleteBoatFailedModal({
  onClose
}: DeleteBoatFailedModalProps) {
  const i18n = useTranslation()
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
      buttonAlignment="center"
      data-testid="delete-boat-failed-modal"
    >
      <ScreenReaderOnly>{i18n.boat.deleteFailed}</ScreenReaderOnly>
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Failure />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            {i18n.boat.deleteFailed}
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
