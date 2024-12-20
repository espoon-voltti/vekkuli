import React, { ReactNode } from 'react'

type ModalContentWrapperProps = {
  children?: ReactNode | ReactNode[]
}
export default React.memo(function ModalContentWrapper({
  children
}: ModalContentWrapperProps) {
  return <div className="modal-wrapper">{children}</div>
})
