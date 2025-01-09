import classNames from 'classnames'
import React from 'react'

export default React.memo(function Container({
  children,
  isBlock = false,
  ...rest
}: {
  children: React.ReactNode
  isBlock?: boolean
  'data-testid'?: string
}) {
  const classes = classNames('container', {
    block: isBlock
  })
  return (
    <div className={classes} {...rest}>
      {children}
    </div>
  )
})
