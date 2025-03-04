import Button, { ButtonType } from 'lib-components/dom/Button'
import React from 'react'

import Buttons, { ButtonAlignment } from '../dom/Buttons'

export interface ModalButton {
  label: string
  action?: () => void
  type?: ButtonType
  loading?: boolean
}

type ModalButtonsProps = {
  buttons?: ModalButton[]
  alignment?: ButtonAlignment
  close: () => void
}

export default React.memo(function ModalButtons({
  buttons,
  close,
  alignment
}: ModalButtonsProps) {
  if (!buttons || buttons.length === 0) return null
  const disableAllButtons = buttons.some((b) => !!b.loading)
  return (
    <div className="mt-l">
      <Buttons alignment={alignment}>
        {buttons.map((b, i) => (
          <Button
            key={`modal-button-${i}`}
            action={b.action || close}
            type={b.type}
            loading={b.loading}
            disabled={disableAllButtons}
          >
            {b.label}
          </Button>
        ))}
      </Buttons>
    </div>
  )
})
