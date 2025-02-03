import classNames from 'classnames'
import React from 'react'

export default React.memo(function Columns({
  children,
  isMultiline = false,
  isVCentered = false,
  verticalPLarge = false,
  is3 = false,
  hideOnMobile,
  bottomMarginOnMobile,
  ...rest
}: {
  children: React.ReactNode
  isMultiline?: boolean
  isVCentered?: boolean
  verticalPLarge?: boolean
  hideOnMobile?: boolean
  is3?: boolean
  bottomMarginOnMobile?: boolean
  'data-testid'?: string
}) {
  const classes = classNames('columns', {
    'is-multiline': isMultiline,
    'is-vcentered': isVCentered,
    'pv-l': verticalPLarge,
    'is-3': is3,
    'is-hidden-mobile': hideOnMobile,
    'mb-xl-mobile': bottomMarginOnMobile
  })
  return (
    <div className={classes} {...rest}>
      {children}
    </div>
  )
})
