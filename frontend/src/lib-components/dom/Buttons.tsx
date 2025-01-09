import classNames from 'classnames'
import React from 'react'

export default React.memo(function Button({
  children,
  centered
}: {
  children: React.ReactNode
  centered?: boolean
}) {
  const classes = ['buttons']
  if (centered) {
    classes.push('is-justify-content-center')
  }

  return <div className={classNames(classes)}>{children}</div>
})
