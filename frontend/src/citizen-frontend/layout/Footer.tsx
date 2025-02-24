import React from 'react'

import { useTranslation } from '../localization'

export const Footer = () => {
  const i18n = useTranslation()
  const privacyLink = i18n.footer.privacyLink
  const boatingLink = i18n.footer.boatingLink
  return (
    <footer className="footer" style={{ background: 'white' }}>
      <div className="content has-text-centered">
        <p>
          <span
            style={{ padding: '16px' }}
            dangerouslySetInnerHTML={{ __html: privacyLink }}
          />
          <span
            style={{ padding: '16px' }}
            dangerouslySetInnerHTML={{ __html: boatingLink }}
          />
        </p>
      </div>
    </footer>
  )
}
