import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Trash } from 'lib-icons'

import IconLink from './IconLink'

export type DeleteLinkProps = {
  children?: React.ReactNode
  action?: () => void
  href?: string
}

export default React.memo(function DeleteLink({
  children,
  action,
  href
}: DeleteLinkProps) {
  const i18n = useTranslation()
  const defaultedChildren = children || i18n.components.links.delete
  return (
    <IconLink icon={<Trash />} action={action} href={href} type="danger">
      {defaultedChildren}
    </IconLink>
  )
})
