import React from 'react'
import { useNavigate } from 'react-router'

import { ChevronLeft } from 'lib-icons'

import { useTranslation } from '../../citizen-frontend/localization'

import IconLink from './IconLink'

export type GoBackLinkProps = {
  children?: React.ReactNode
  action?: () => void
  href?: string
}

export default React.memo(function GoBackLink({
  children,
  action,
  href
}: GoBackLinkProps) {
  const navigate = useNavigate()
  const i18n = useTranslation()
  const defaultedAction = !action && !href ? () => navigate(-1) : action
  const defaultedChildren = children || i18n.components.links.goBack
  return (
    <IconLink icon={<ChevronLeft />} action={defaultedAction} href={href}>
      {defaultedChildren}
    </IconLink>
  )
})
