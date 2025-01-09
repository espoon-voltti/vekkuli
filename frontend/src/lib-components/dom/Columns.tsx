import classNames from 'classnames'
import React from 'react'

export default React.memo(function Columns({
  children,
  isMultiline = false,
  isVCentered = false,
  verticalPLarge = false,
  is3 = false
}: {
  children: React.ReactNode
  isMultiline?: boolean
  isVCentered?: boolean
  verticalPLarge?: boolean
  is3?: boolean
}) {
  const classes = classNames('columns', {
    'is-multiline': isMultiline,
    'is-vcentered': isVCentered,
    'pv-l': verticalPLarge,
    'is-3': is3
  })
  return <div className={classes}>{children}</div>
})
