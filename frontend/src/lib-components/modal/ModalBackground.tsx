import React, { ReactNode } from 'react'
import { FocusOn } from 'react-focus-on'

type ModalBackgroundProps = {
  close: () => void
  children?: ReactNode | ReactNode[]
}
export default React.memo(function ModalBackground({
  close,
  children
}: ModalBackgroundProps) {
  return (
    <FocusOn onEscapeKey={close} className="modal">
      <div className="modal-underlay" onClick={close} />
      {children}
    </FocusOn>
  )
})
