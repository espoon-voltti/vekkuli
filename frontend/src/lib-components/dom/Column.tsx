import classNames from 'classnames'
import React from 'react'

export type ColumnProps = {
  children: React.ReactNode
  isNarrow?: boolean
  isFull?: boolean
  isHalf?: boolean
  isOneThird?: boolean
  isOneQuarter?: boolean
  isTwoFifths?: boolean
  isOneEight?: boolean
  toRight?: boolean
  textCentered?: boolean
}

export default React.memo(function Column({
  children,
  isNarrow = false,
  isFull = false,
  isHalf = false,
  isOneThird = false,
  isOneQuarter = false,
  isTwoFifths = false,
  isOneEight = false,
  toRight = false,
  textCentered = false
}: ColumnProps) {
  const classes = classNames('column', {
    'is-narrow': isNarrow,
    'is-full': isFull,
    'is-half': isHalf,
    'is-one-third': isOneThird,
    'is-one-quarter': isOneQuarter,
    'is-one-eight': isOneEight,
    'is-two-fifths': isTwoFifths,
    'ml-auto': toRight,
    'has-text-centered': textCentered
  })
  return <div className={classes}>{children}</div>
})
