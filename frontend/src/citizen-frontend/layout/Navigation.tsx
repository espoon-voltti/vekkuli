import React from 'react'
import { Link } from 'react-router'

import Logo from 'lib-customizations/espoo/assets/EspooLogoPrimary.svg'

import { useTranslation } from '../localization'

import LanguageSelection from './LanguageSelection'
import Menu from './Menu'

export default React.memo(function Navigation() {
  const i18n = useTranslation()
  return (
    <nav role="navigation" aria-label="main navigation">
      <div className="nav-row">
        <div className="columns">
          <img className="logo" src={Logo} alt="Espoo logo" />
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
            Venepaikat
          </Link>
        </div>
        <div>
          <Link className="link" to="/kuntalainen/omat-tiedot">
            Omat tiedot
          </Link>
        </div>
      </div>
    </nav>
  )
})
