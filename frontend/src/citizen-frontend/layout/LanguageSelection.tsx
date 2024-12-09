import React from 'react'

import { Globe } from 'lib-icons'

import { useLang, useTranslation } from '../localization'

export default React.memo(function LanguageSelection() {
  const i18n = useTranslation()
  const [lang, setLang] = useLang()
  return (
    <div className="dropdown is-hoverable" id="language-selection">
      <div className="dropdown-trigger">
        <a
          className="dropdown-title"
          aria-haspopup="true"
          aria-controls="dropdown-menu"
        >
          <span className="icon is-small">
            <Globe />
          </span>
          <span className="pl-xs">{lang.toUpperCase()}</span>
        </a>
      </div>
      <div className="dropdown-menu" id="dropdown-menu" role="menu">
        <div className="dropdown-content">
          <a className="dropdown-item" onClick={() => setLang('fi')}>
            {i18n.common.unit.languagesShort.fi}
          </a>
          <a className="dropdown-item" onClick={() => setLang('sv')}>
            {i18n.common.unit.languagesShort.sv}
          </a>
          <a className="dropdown-item" onClick={() => setLang('en')}>
            {i18n.common.unit.languagesShort.en}
          </a>
        </div>
      </div>
    </div>
  )
})
