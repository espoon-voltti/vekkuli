import classNames from 'classnames'
import React from 'react'

export default React.memo(function Columns({
  children,
  isMultiline = false
}: {
  children: React.ReactNode
  isMultiline?: boolean
}) {
  const classes = classNames('columns', {
    'is-multiline': isMultiline
  })
  return <div className={classes}>{children}</div>
})
