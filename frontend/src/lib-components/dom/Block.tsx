import classNames from 'classnames'
import React from 'react'

export type BlockProps = {
  children: React.ReactNode
}

export default React.memo(function Block({ children }: BlockProps) {
  const classes = classNames('block')
  return <div className={classes}>{children}</div>
})
