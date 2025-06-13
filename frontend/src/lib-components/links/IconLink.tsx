import classNames from 'classnames'
import React from 'react'

export type IconLinkType = 'primary' | 'danger'

export type IconLinkProps = {
  children: React.ReactNode
  icon: React.ReactNode
  action?: () => void
  href?: string
  ariaLabel?: string
  type?: IconLinkType
  dataTestId?: string
}

export default React.memo(function IconLink({
  children,
  icon,
  action,
  href,
  ariaLabel,
  type,
  dataTestId
}: IconLinkProps) {
  if (href) {
    return (
      <a
        data-testid={dataTestId}
        href={href}
        className={iconLinkClasses(type)}
        aria-label={ariaLabel}
        role="link"
      >
        <span className="icon">{icon}</span>
        <span>{children}</span>
      </a>
    )
  }

  return (
    <button
      data-testid={dataTestId}
      onClick={action}
      className={iconLinkClasses(type, 'has-text-link')}
      aria-label={ariaLabel}
      role="button"
    >
      <span className="icon">{icon}</span>
      <span>{children}</span>
    </button>
  )
})

function iconLinkClasses(type?: IconLinkType, ...extra: string[]): string {
  const classes = ['is-link', 'is-icon-link', ...extra]

  switch (type) {
    case 'primary':
      classes.push('has-text-primary')
      break
    case 'danger':
      classes.push('has-text-danger')
      break
  }

  return classNames(classes)
}
