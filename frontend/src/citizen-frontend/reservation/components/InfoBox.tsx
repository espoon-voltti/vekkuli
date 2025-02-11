import classNames from 'classnames'
import React from 'react'

import { BlueInfoCircle } from 'lib-icons'

type InfoBoxProps = {
  text: string
  fullWidth?: boolean
}

export const InfoBox = React.memo(function InfoBox({
  text,
  fullWidth
}: InfoBoxProps) {
  const classes = classNames('message-box is-info column', {
    'is-four-fifths': !fullWidth
  })

  return (
    <div className={classes}>
      <div className="column is-narrow">
        <span className="icon">
          <BlueInfoCircle />
        </span>
      </div>
      <p className="column">{text}</p>
    </div>
  )
})
