import classNames from 'classnames'
import React from 'react'

export type ColumnProps = {
  children: React.ReactNode
  isNarrow?: boolean
  isFull?: boolean
  isHalf?: boolean
  isOneThird?: boolean
  isOneQuarter?: boolean
  isOneEight?: boolean
}

export default React.memo(function Column({
  children,
  isNarrow = false,
  isFull = false,
  isHalf = false,
  isOneThird = false,
  isOneQuarter = false,
  isOneEight = false
}: ColumnProps) {
  const classes = classNames('column', {
    'is-narrow': isNarrow,
    'is-full': isFull,
    'is-half': isHalf,
    'is-one-third': isOneThird,
    'is-one-quarter': isOneQuarter,
    'is-one-eight': isOneEight
  })
  return <div className={classes}>{children}</div>
})
