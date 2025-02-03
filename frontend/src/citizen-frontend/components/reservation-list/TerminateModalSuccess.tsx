import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { Success } from 'lib-icons'

export type TerminateModalProps = {
  close: () => void
}

export default React.memo(function TerminateModalSuccess({
  close
}: TerminateModalProps) {
  setTimeout(() => close(), 3000)
  return (
    <Modal close={close} data-testid="terminate-reservation-success-modal">
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Success />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            Paikka irtisanottu onnistuneesti
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
