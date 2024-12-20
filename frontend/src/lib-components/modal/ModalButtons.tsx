import Button, { ButtonType } from 'lib-components/dom/Button'
import React from 'react'

import Buttons from '../dom/Buttons'

export interface ModalButton {
  label: string
  action?: () => void
  type?: ButtonType
  loading?: boolean
}

type ModalButtonsProps = {
  buttons?: ModalButton[]
  centered?: boolean
  close: () => void
}

export default React.memo(function ModalButtons({
  buttons,
  close,
  centered
}: ModalButtonsProps) {
  if (!buttons || buttons.length === 0) return null
  return (
    <div className="mt-l">
      <Buttons centered={centered}>
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
    </div>
  )
})
