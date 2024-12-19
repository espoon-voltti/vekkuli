import classNames from 'classnames'
import React from 'react'

export default React.memo(function Columns({
  children,
  isMultiline = false,
  isVcentered = false
}: {
  children: React.ReactNode
  isMultiline?: boolean
  isVcentered?: boolean
}) {
  const classes = classNames('columns', {
    'is-multiline': isMultiline,
    'is-vcentered': isVcentered
  })
  return <div className={classes}>{children}</div>
})
