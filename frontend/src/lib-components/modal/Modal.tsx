import React from 'react'

import { ButtonType } from '../dom/Button'

import ModalBackground from './ModalBackground'
import ModalButtons from './ModalButtons'
import ModalContent from './ModalContent'
import ModalTitle from './ModalTitle'
import ModalWrapper from './ModalWrapper'

type ModalProperties = {
  title?: string
  children: React.ReactNode
  close: () => void
  buttons?: ModalButton[]
}

export default React.memo(function Modal({
  children,
  title,
  close,
  buttons
}: ModalProperties) {
  return (
    <ModalBackground close={close}>
      <ModalWrapper>
        <ModalTitle title={title} />
        <ModalContent>{children}</ModalContent>
        <ModalButtons buttons={buttons} close={close} />
      </ModalWrapper>
    </ModalBackground>
  )
})

export interface ModalButton {
  label: string
  action?: () => void
  type?: ButtonType
}
