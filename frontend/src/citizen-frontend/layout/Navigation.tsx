import React from 'react'

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
          <a className="link" href="/kuntalainen/venepaikka">
            Venepaikat
          </a>
        </div>
        <div>
          <a className="link" href="/kuntalainen/omat-tiedot">
            Omat tiedot
          </a>
        </div>
      </div>
    </nav>
  )
})
