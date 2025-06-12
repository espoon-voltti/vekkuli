import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Edit } from 'lib-icons'

import IconLink from './IconLink'

export type EditLinkProps = {
  children?: React.ReactNode
  action?: () => void
  href?: string
  showText?: boolean
}

export default React.memo(function EditLink({
  children,
  action,
  href,
  showText
}: EditLinkProps) {
  const i18n = useTranslation()
  const defaultText = i18n.common.edit
  const defaultedChildren = showText ? children || defaultText : undefined
  return (
    <IconLink icon={<Edit />} action={action} href={href}>
      {defaultedChildren}
    </IconLink>
  )
})
