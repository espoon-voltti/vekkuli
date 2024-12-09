import classNames from 'classnames'
import React from 'react'

export type ButtonType = 'primary' | 'secondary' | 'danger'

export type ButtonProps = {
  type?: ButtonType
  action?: () => void
  children: React.ReactNode
}

export default React.memo(function Button({
  children,
  action,
  type
}: ButtonProps) {
  const classes = ['button']
  switch (type) {
    case 'primary':
      classes.push('is-primary')
      break
    case 'danger':
      classes.push('is-danger')
      break
  }

  const props: React.ButtonHTMLAttributes<HTMLButtonElement> = {
    className: classNames(classes)
  }

  if (action) {
    props.onClick = action
  }

  return <button {...props}>{children}</button>
})
