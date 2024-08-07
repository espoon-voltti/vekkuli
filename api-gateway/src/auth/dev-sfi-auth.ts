// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import _ from 'lodash'
import { Request, Router, urlencoded } from 'express'
import {
  assertStringProp,
  AsyncRequestHandler,
  toRequestHandler
} from '../utils/express.js'
import { citizenLogin, CitizenUser } from '../clients/service-client.js'
import { Sessions } from './session.js'
import passport, { Strategy } from 'passport'
import { AppSessionUser, authenticate, login, logout } from './index.js'
import { appBaseUrl } from '../config.js'
import { parseRelayState } from './saml/common.js'

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
    address: { finnish: 'Ahertajankuja 1', swedish: 'Flitargränden 1' },
    town: { finnish: 'Espoo', swedish: 'Esbo' }
    // homeTown: 49,
  },
  {
    nationalId: '031298-988S',
    firstName: 'Olivia',
    lastName: 'Virtanen',
    postalCode: '02130',
    address: {
      finnish: 'Hämeenkyläntie 2B 56',
      swedish: 'Tavastbyvägen 2B 56'
    },
    town: { finnish: 'Espoo', swedish: 'Esbo' }
    // homeTown: 49,
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
                  <button type="submit">Kirjaudu</button>
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
        const user = await authenticate(strategyName, req, res)
        if (!user) {
          res.redirect(`${appBaseUrl}?loginError=true`)
        } else {
          await login(req, user)
          const redirectUrl = parseRelayState(req) ?? '/'
          res.redirect(redirectUrl)
        }
      } catch (err) {
        if (!res.headersSent) {
          res.redirect(`${appBaseUrl}?loginError=true`)
        }
        throw err
      }
    })
  )

  router.get(
    `/logout`,
    toRequestHandler(async (req, res) => {
      await logout(sessions, req, res)
      res.redirect('/')
    })
  )

  return router
}
