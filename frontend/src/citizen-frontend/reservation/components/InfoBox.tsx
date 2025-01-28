import React from 'react'

import { BlueInfoCircle } from 'lib-icons'

export const InfoBox = React.memo(function InfoBox({ text }: { text: string }) {
  return (
    <div
      id="empty-dimensions-warning"
      className="message-box is-info column is-four-fifths"
    >
      <div className="column is-narrow">
        <span className="icon">
          <BlueInfoCircle />
        </span>
      </div>
      <p className="column">{text}</p>
    </div>
  )
})
