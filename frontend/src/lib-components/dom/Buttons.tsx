import classNames from 'classnames'
import React from 'react'

export type ButtonAlignment = 'left' | 'center' | 'right'

export default React.memo(function Button({
  children,
  alignment
}: {
  children: React.ReactNode
  alignment?: ButtonAlignment
}) {
  const classes = ['buttons']
  if (alignment === 'center') {
    classes.push('is-justify-content-center')
  } else if (alignment === 'right') {
    classes.push('is-justify-content-right')
  }

  return <div className={classNames(classes)}>{children}</div>
})
