import classNames from 'classnames'
import React from 'react'

export default React.memo(function Container({
  children,
  isBlock = false
}: {
  children: React.ReactNode
  isBlock?: boolean
}) {
  const classes = classNames('container', {
    block: isBlock
  })
  return <div className={classes}>{children}</div>
})
