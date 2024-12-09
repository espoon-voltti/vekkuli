import React, { ReactNode } from 'react'

type ModalWrapperProps = {
  children?: ReactNode | ReactNode[]
}
export default React.memo(function ModalWrapper({
  children
}: ModalWrapperProps) {
  return <div className="modal-wrapper">{children}</div>
})
