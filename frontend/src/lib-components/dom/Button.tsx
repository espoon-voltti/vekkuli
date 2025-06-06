import classNames from 'classnames'
import React from 'react'

export type ButtonType = 'primary' | 'secondary' | 'danger' | 'danger-outlined'

export type ButtonProps = {
  children: React.ReactNode
  id?: string
  type?: ButtonType
  isFullWidth?: boolean
  action?: () => void
  loading?: boolean
  ariaLabel?: string
  disabled?: boolean
}

export default React.memo(function Button({
  children,
  action,
  id,
  type,
  isFullWidth,
  loading,
  ariaLabel,
  disabled
}: ButtonProps) {
  const classes = ['button']
  switch (type) {
    case 'primary':
      classes.push('is-primary')
      break
    case 'danger':
      classes.push('is-danger')
      break
    case 'danger-outlined':
      classes.push('is-danger')
      classes.push('is-outlined')
      break
  }

  if (isFullWidth) {
    classes.push('is-fullwidth')
  }

  if (loading) {
    classes.push('is-loading')
  }

  const props: React.ButtonHTMLAttributes<HTMLButtonElement> = {
    className: classNames(classes),
    disabled: !!disabled
  }

  if (action && !loading && !disabled) {
    props.onClick = action
  }

  if (id) {
    props.id = id
  }

  return (
    <button role="button" aria-label={ariaLabel} {...props}>
      {children}
    </button>
  )
})
