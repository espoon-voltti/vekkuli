// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
import type { CitizenCustomizations } from 'lib-customizations/types'

import { citizenConfig } from './appConfigs'
import EspooLogo from './assets/EspooLogoPrimary.svg'

const customizations: CitizenCustomizations = {
  appConfig: citizenConfig,
  langs: ['fi', 'sv', 'en'],
  translations: {
    fi: {},
    sv: {},
    en: {}
  },
  cityLogo: {
    src: EspooLogo,
    alt: 'Espoo Logo'
  }
}

export default customizations
