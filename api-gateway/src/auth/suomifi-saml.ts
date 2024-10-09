// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { z } from 'zod'
import { SamlConfig, Strategy } from '@node-saml/passport-saml'

import { createSamlStrategy } from './saml/common.js'
import { Sessions } from './session.js'
import { citizenLogin } from '../clients/service-client.js'
import { logWarn } from '../logging/index.js'

// Suomi.fi e-Identification – Attributes transmitted on an identified user:
//   https://esuomi.fi/suomi-fi-services/suomi-fi-e-identification/14247-2/?lang=en
// Note: Suomi.fi only returns the values we request in our SAML metadata
const SUOMI_FI_SSN_KEY = 'urn:oid:1.2.246.21'
const SUOMI_FI_GIVEN_NAME_KEY = 'urn:oid:2.5.4.42'
const SUOMI_FI_SURNAME_KEY = 'urn:oid:2.5.4.4'
const SUOMI_FI_HOME_TOWN_KEY = 'urn:oid:1.2.246.517.2002.2.18'
const SUOMI_FI_STREET_ADDRESS_FI = 'urn:oid:1.2.246.517.2002.2.4'
const SUOMI_FI_STREET_ADDRESS_SV = 'urn:oid:1.2.246.517.2002.2.5'
const SUOMI_FI_POSTAL_CODE = 'urn:oid:1.2.246.517.2002.2.6'
const SUOMI_FI_POST_OFFICE_FI = 'urn:oid:1.2.246.517.2002.2.7'
const SUOMI_FI_POST_OFFICE_SV = 'urn:oid:1.2.246.517.2002.2.8'
const SUOMI_FI_EMAIL = 'urn:oid:0.9.2342.19200300.100.1.3'
const SUOMI_FI_DATA_PROTECTION = 'urn:oid:1.2.246.517.2002.2.27'

const Profile = z
  .object({
    [SUOMI_FI_SSN_KEY]: z.string(),
    [SUOMI_FI_GIVEN_NAME_KEY]: z.string(),
    [SUOMI_FI_SURNAME_KEY]: z.string(),
    [SUOMI_FI_HOME_TOWN_KEY]: z.coerce.number(),
    [SUOMI_FI_STREET_ADDRESS_FI]: z.string(),
    [SUOMI_FI_STREET_ADDRESS_SV]: z.string(),
    [SUOMI_FI_POSTAL_CODE]: z.string(),
    [SUOMI_FI_POST_OFFICE_FI]: z.string(),
    [SUOMI_FI_POST_OFFICE_SV]: z.string(),
    [SUOMI_FI_EMAIL]: z.string(),
    [SUOMI_FI_DATA_PROTECTION]: z.coerce.number()
  })
  .partial({
    [SUOMI_FI_HOME_TOWN_KEY]: true,
    [SUOMI_FI_STREET_ADDRESS_FI]: true,
    [SUOMI_FI_STREET_ADDRESS_SV]: true,
    [SUOMI_FI_POSTAL_CODE]: true,
    [SUOMI_FI_POST_OFFICE_FI]: true,
    [SUOMI_FI_POST_OFFICE_SV]: true,
    [SUOMI_FI_EMAIL]: true,
    [SUOMI_FI_DATA_PROTECTION]: true
  })

const ssnRegex = /^[0-9]{6}[-+ABCDEFUVWXY][0-9]{3}[0-9ABCDEFHJKLMNPRSTUVWXY]$/

export function createSuomiFiStrategy(
  sessions: Sessions,
  config: SamlConfig
): Strategy {
  return createSamlStrategy(sessions, config, Profile, async (profile) => {
    const socialSecurityNumber = profile[SUOMI_FI_SSN_KEY]?.trim()
    if (!socialSecurityNumber) throw Error('No SSN in SAML data')
    if (!ssnRegex.test(socialSecurityNumber)) {
      logWarn('Invalid SSN received from Suomi.fi login')
    }

    return await citizenLogin({
      nationalId: socialSecurityNumber,
      firstName: profile[SUOMI_FI_GIVEN_NAME_KEY]?.trim() ?? '',
      lastName: profile[SUOMI_FI_SURNAME_KEY]?.trim() ?? '',
      municipalityCode: profile[SUOMI_FI_HOME_TOWN_KEY] ?? 1,
      address: {
        fi: profile[SUOMI_FI_STREET_ADDRESS_FI] ?? '',
        sv: profile[SUOMI_FI_STREET_ADDRESS_SV] ?? ''
      },
      postalCode: profile[SUOMI_FI_POSTAL_CODE],
      postOffice: {
        fi: profile[SUOMI_FI_POST_OFFICE_FI] ?? '',
        sv: profile[SUOMI_FI_POST_OFFICE_SV] ?? ''
      },
      dataProtection: profile[SUOMI_FI_DATA_PROTECTION] === 1
    })
  })
}
