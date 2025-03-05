import React from 'react'
import { Link } from 'react-router'

import Logo from 'lib-customizations/vekkuli/assets/VenepaikkavarausLogo.svg'

import { useTranslation } from '../localization'

import LanguageSelection from './LanguageSelection'
import Menu from './Menu'
import {OpenInNew} from "../../lib-icons"

export default React.memo(function Navigation() {
  const i18n = useTranslation()

  const InstructionsLink = () => (
    <div className="is-primary-color ">
      <Link to={"https://admin.espoo.fi/sites/default/files/2025-03/Venepaikan%20varaaminen%20ja%20vaihtaminen.pdf"} className="link open-in-new-link" aria-label={i18n.header.openInANewWindow} target="_blank">
        <span>{i18n.header.instructionsLink}</span>
        <OpenInNew />
      </Link>
    </div>
  )

  return (
    <nav role="navigation" aria-label={i18n.header.mainNavigation}>
      <div className="nav-row">
        <div className="columns">
          <Link to="/" className="link" aria-label={i18n.header.goToHomepage}>
            <img className="logo" src={Logo} alt="Espoo logo" />
          </Link>
          <h1 className="is-primary-color">{i18n.common.title}</h1>
        </div>
        <div className="columns">
          <InstructionsLink />
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
