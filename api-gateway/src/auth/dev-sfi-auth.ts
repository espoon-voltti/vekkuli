// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Request, Router, urlencoded } from 'express'
import _ from 'lodash'
import passport, { Strategy } from 'passport'
import { citizenLogin, CitizenUser } from '../clients/service-client.js'
import { citizenRootUrl } from '../config.js'
import { logWarn } from '../logging/index.js'
import { errorOrUndefined } from '../utils/errorOrUndefined.js'
import {
  assertStringProp,
  AsyncRequestHandler,
  toRequestHandler
} from '../utils/express.js'
import { AppSessionUser, authenticate, login, logout } from './index.js'
import { injectLoginErrorToUrl } from './saml/common.js'
import { getRedirectUrl } from './saml/saml-routes.js'
import { Sessions } from './session.js'

class DevStrategy extends Strategy {
  constructor(private verifyUser: (req: Request) => Promise<AppSessionUser>) {
    super()
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  authenticate(req: Request, _options?: any): any {
    this.verifyUser(req)
      .then((user) => this.success(user))
      .catch((err) => this.error(err))
  }
}

const devUsers: CitizenUser[] = [
  {
    nationalId: '150499-911U',
    firstName: 'Leo',
    lastName: 'Korhonen',
    postalCode: '00370',
    address: { fi: 'Ahertajankuja 1', sv: 'Flitargränden 1' },
    municipalityCode: 49,
    postOffice: { fi: 'Espoo', sv: 'Esbo' },
    dataProtection: false
  },
  {
    nationalId: '031298-988S',
    firstName: 'Olivia',
    lastName: 'Virtanen',
    postalCode: '02130',
    address: {
      fi: 'Hämeenkyläntie 2B 56',
      sv: 'Tavastbyvägen 2B 56'
    },
    municipalityCode: 49,
    postOffice: { fi: 'Espoo', sv: 'Esbo' },
    dataProtection: false
  },
  {
    nationalId: '010106A957V',
    firstName: 'Mikko',
    lastName: 'Virtanen',
    postalCode: '02130',
    address: {
      fi: 'Hämeenkyläntie 2B 56',
      sv: 'Tavastbyvägen 2B 56'
    },
    municipalityCode: 49,
    postOffice: { fi: 'Espoo', sv: 'Esbo' },
    dataProtection: false
  },
  {
    nationalId: '111275-180K',
    firstName: 'Jorma',
    lastName: 'Kääriäinen',
    postalCode: '15100',
    address: {
      fi: 'Businesscity 2B 56',
      sv: 'Businesscity 2B 56'
    },
    municipalityCode: 398,
    postOffice: { fi: 'Lahti', sv: 'Lahtis' },
    dataProtection: false
  },
  {
    nationalId: '290991-993F',
    firstName: 'Marko',
    lastName: 'Kuusinen',
    postalCode: '00270',
    address: {
      fi: 'Kuusitie 21',
      sv: 'Granvägen 21'
    },
    municipalityCode: 91,
    postOffice: { fi: 'Helsinki', sv: 'Helsingfors' },
    dataProtection: false
  }
]

const loginFormHandler: AsyncRequestHandler = async (req, res) => {
  const userOptions = devUsers.map((user, idx) => {
    const { nationalId, firstName, lastName } = user
    const json = JSON.stringify(user)
    return `<div>
            <input
              type="radio"
              id="${nationalId}"
              data-testid="${nationalId}"
              name="preset"
              ${idx == 0 ? 'checked' : ''}
              value="${_.escape(json)}" />
            <label for="${nationalId}">${firstName} ${lastName}</label>
          </div>`
  })

  const formQuery =
    typeof req.query.RelayState === 'string'
      ? `?RelayState=${encodeURIComponent(req.query.RelayState)}`
      : ''
  const formUri = `${req.baseUrl}/login/callback${formQuery}`

  res.contentType('text/html').send(`
          <html lang='fi'>
          <body>
            <h1>Devausympäristön AD-kirjautuminen</h1>
            <form action="${formUri}" method="post">
                ${userOptions.join('\n')}
                <div style="margin-top: 20px">
                  <button type="submit" name="action" value="submit">Kirjaudu</button>
                  <button type="submit" name="action" value="cancel">Peruuta</button>
                </div>
            </form>
          </body>
          </html>
        `)
}

const verifyUser = async (req: Request): Promise<AppSessionUser> => {
  const preset = assertStringProp(req.body, 'preset')
  const person = await citizenLogin(JSON.parse(preset))
  return {
    id: person.id,
    type: 'citizen'
  }
}

export function createDevSfiRouter(sessions: Sessions): Router {
  const strategyName = 'dev-sfi'
  passport.use(strategyName, new DevStrategy(verifyUser))

  const router = Router()

  router.get('/login', toRequestHandler(loginFormHandler))
  router.post(
    `/login/callback`,
    urlencoded({ extended: false }), // needed to parse the POSTed form
    toRequestHandler(async (req, res) => {
      try {
        if (req.body.action === 'cancel') {
          res.redirect(injectLoginErrorToUrl(getRedirectUrl('citizen', req)))
          return
        }

        const user = await authenticate(strategyName, req, res)
        if (!user) {
          res.redirect(injectLoginErrorToUrl(getRedirectUrl('citizen', req)))
        } else {
          await login(req, user)
          res.redirect(getRedirectUrl('citizen', req))
        }
      } catch (err) {
        if (!res.headersSent) {
          res.redirect(injectLoginErrorToUrl(getRedirectUrl('citizen', req)))
        }
        throw err
      }
    })
  )

  router.get(
    `/logout`,
    toRequestHandler(async (req, res) => {
      try {
        await logout(sessions, req, res)
      } catch (error) {
        logWarn('Logout failed', req, undefined, errorOrUndefined(error))
      } finally {
        if (!res.headersSent) {
          res.redirect(citizenRootUrl)
        }
      }
    })
  )

  return router
}
