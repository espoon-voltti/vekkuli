import React, { useEffect, useRef } from 'react'

import { WarningExclamation } from 'lib-icons/vekkuli-icons'

export const ErrorBox = React.memo(function ErrorBox({
  text
}: {
  text: string
}) {
  // Set focus with delay explicitly to the error box when it is rendered to
  // make sure that screen reader users are notified about the error.
  const errorBoxRef = useRef<HTMLDivElement>(null)
  useEffect(() => {
    setTimeout(() => {
      requestAnimationFrame(() => {
        errorBoxRef.current?.focus()
      })
    }, 250)
  }, [])
  return (
    <div
      className="message-box is-error column is-four-fifths"
      ref={errorBoxRef}
      tabIndex={-1}
    >
      <div className="column is-narrow" aria-hidden="true">
        <span className="icon">
          <WarningExclamation isError={false} />
        </span>
      </div>
      <p role="alert" className="column">
        {text}
      </p>
    </div>
  )
})
