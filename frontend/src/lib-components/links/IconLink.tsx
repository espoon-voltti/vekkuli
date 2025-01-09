import React from 'react'

export type IconLinkProps = {
  children: React.ReactNode
  icon: React.ReactNode
  action?: () => void
  href?: string
}

export default React.memo(function IconLink({
  children,
  icon,
  action,
  href
}: IconLinkProps) {
  const props: React.AnchorHTMLAttributes<HTMLAnchorElement> = {
    className: 'is-link is-icon-link'
  }
  if (action) {
    props.onClick = action
  }
  if (href) {
    props.href = href
  }

  return (
    <a {...props}>
      <span className="icon">{icon}</span>
      <span>{children}</span>
    </a>
  )
})
