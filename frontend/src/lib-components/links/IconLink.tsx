import React from 'react'

export type IconLinkProps = {
  children: React.ReactNode
  icon: React.ReactNode
  action?: () => void
  href?: string
  ariaLabel?: string
}

export default React.memo(function IconLink({
  children,
  icon,
  action,
  href,
  ariaLabel
}: IconLinkProps) {
  if (href) {
    return (
      <a
        href={href}
        className="is-link is-icon-link"
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
      onClick={action}
      className="has-text-link is-link is-icon-link"
      aria-label={ariaLabel}
      role="button"
    >
      <span className="icon">{icon}</span>
      <span>{children}</span>
    </button>
  )
})
