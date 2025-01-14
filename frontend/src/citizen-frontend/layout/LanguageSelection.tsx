import { Dropdown } from 'lib-components/dom'
import React from 'react'

import { Lang } from 'lib-customizations/vekkuli/citizen'
import { Globe } from 'lib-icons'

import { useLang, useTranslation } from '../localization'

const menuItemData: Lang[] = ['fi', 'sv', 'en']

export default React.memo(function LanguageSelection() {
  const i18n = useTranslation()
  const [lang, setLang] = useLang()
  return (
    <Dropdown
      isHoverable
      id="language-selection"
      ariaLabel={i18n.header.selectLanguage}
    >
      {{
        label: (
          <>
            <span className="icon is-small">
              <Globe />
            </span>
            <span className="pl-xs">{lang.toUpperCase()}</span>
          </>
        ),
        menuItems: (
          <>
            {menuItemData.map((lang) => (
              <button
                key={lang}
                role="menuitem"
                className="dropdown-item"
                onClick={() => setLang(lang)}
              >
                {i18n.common.unit.languagesShort[lang]}
              </button>
            ))}
          </>
        )
      }}
    </Dropdown>
  )
})
