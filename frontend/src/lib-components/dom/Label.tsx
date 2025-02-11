import classNames from 'classnames'
import React from 'react'

export type BlockProps = {
  children: React.ReactNode
}

export default React.memo(function Label({ children }: BlockProps) {
  const classes = classNames('label')
  return <label className={classes}>{children}</label>
})
