import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Edit } from 'lib-icons'

import IconLink from './IconLink'

export type EditLinkProps = {
  children?: React.ReactNode
  action?: () => void
  href?: string
}

export default React.memo(function EditLink({
  children,
  action,
  href
}: EditLinkProps) {
  const i18n = useTranslation()
  const defaultedChildren = children || i18n.components.links.edit
  return (
    <IconLink icon={<Edit />} action={action} href={href}>
      {defaultedChildren}
    </IconLink>
  )
})
