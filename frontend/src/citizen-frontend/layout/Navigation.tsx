import React from 'react'
import { Link } from 'react-router'

import Logo from 'lib-customizations/espoo/assets/EspooLogoPrimary.svg'

import { useTranslation } from '../localization'

import LanguageSelection from './LanguageSelection'
import Menu from './Menu'

export default React.memo(function Navigation() {
  const i18n = useTranslation()
  return (
    <nav role="navigation" aria-label={i18n.header.mainNavigation}>
      <div className="nav-row">
        <div className="columns">
          <Link to="/" className="link" aria-label={i18n.header.goToHomepage}>
            <img className="logo" src={Logo} alt="Espoo logo" />
          </Link>
          <h1>{i18n.common.title}</h1>
        </div>
        <div className="columns">
          <LanguageSelection />
          <Menu />
        </div>
      </div>

      <div className="nav-row">
        <div>
          <Link className="link" to="/kuntalainen/venepaikka">
            {i18n.citizenFrontPage.title}
          </Link>
        </div>
        <div>
          <Link className="link" to="/kuntalainen/omat-tiedot">
            {i18n.citizenPage.title}
          </Link>
        </div>
      </div>
    </nav>
  )
})
