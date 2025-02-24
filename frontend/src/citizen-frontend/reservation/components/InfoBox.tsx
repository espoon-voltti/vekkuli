import classNames from 'classnames'
import React from 'react'

import { BlueInfoCircle, WarningExclamation } from 'lib-icons'

type InfoBoxProps = {
  text: string
  fullWidth?: boolean
  isWarning?: boolean
}

export const InfoBox = React.memo(function InfoBox({
  text,
  fullWidth,
  isWarning
}: InfoBoxProps) {
  const classes = classNames('message-box column', {
    'is-four-fifths': !fullWidth,
    [isWarning ? 'is-warning' : 'is-info']: true
  })

  return (
    <div className={classes}>
      <div className="column is-narrow">
        <span className="icon">
          {isWarning ? (
            <WarningExclamation isError={false} />
          ) : (
            <BlueInfoCircle />
          )}
        </span>
      </div>
      <p className="column">{text}</p>
    </div>
  )
})
