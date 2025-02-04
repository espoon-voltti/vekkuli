import React from 'react'
import ReactDOM from 'react-dom'

const container = document.createElement('div')
document.body.prepend(container)

export default React.memo(function SkipToContentLink({
  children,
  target
}: {
  children: React.ReactNode
  target: string
}) {
  return ReactDOM.createPortal(
    <a
      href={`#${target}`}
      className="button is-primary skip-to-main-content-link"
      tabIndex={0}
    >
      {children}
    </a>,
    container
  )
})
