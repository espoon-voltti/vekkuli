import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import Logo from 'lib-customizations/espoo/assets/EspooLogoPrimary.svg'

export const Footer = () => {
  const i18n = useTranslation()
  const privacyLink = i18n.footer.privacyLink
  const boatingLink = i18n.footer.boatingLink
  return (
    <footer className="footer is-relative" style={{ background: 'white' }}>
      <p className="is-flex is-justify-content-center is-align-items-center is-gap-2">
        <span dangerouslySetInnerHTML={{ __html: privacyLink }} />
        <span dangerouslySetInnerHTML={{ __html: boatingLink }} />
      </p>
      <img className="logo hid" src={Logo} alt="Espoo logo" />
    </footer>
  )
}
