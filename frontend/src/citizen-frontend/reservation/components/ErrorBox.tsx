import React from 'react'

import { WarningExclamation } from 'lib-icons/vekkuli-icons'

export const ErrorBox = React.memo(function ErrorBox({
  text
}: {
  text: string
}) {
  return (
    <div className="message-box is-error column is-four-fifths">
      <div className="column is-narrow">
        <span className="icon">
          <WarningExclamation isError={false} />
        </span>
      </div>
      <p className="column">{text}</p>
    </div>
  )
})
