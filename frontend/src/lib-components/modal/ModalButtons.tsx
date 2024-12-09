import Button from 'lib-components/dom/Button'
import React from 'react'

import Buttons from '../dom/Buttons'

import { ModalButton } from './Modal'

type ModalButtonsProps = {
  buttons?: ModalButton[]
  close: () => void
}
export default React.memo(function ModalButtons({
  buttons,
  close
}: ModalButtonsProps) {
  if (!buttons || buttons.length === 0) return null
  return (
    <Buttons>
      {buttons.map((b, i) => (
        <Button
          key={`modal-button-${i}`}
          action={b.action || close}
          type={b.type}
        >
          {b.label}
        </Button>
      ))}
    </Buttons>
  )
})
