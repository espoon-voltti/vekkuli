import passportSaml from '@node-saml/passport-saml'
import type {
  AuthenticateOptions,
  RequestWithUser
} from '@node-saml/passport-saml/lib/types.js'
import express, { Router, urlencoded } from 'express'
import { ErrorRequestHandler } from 'express-serve-static-core'
import passport from 'passport'
import { citizenRootUrl, employeeRootUrl } from '../../config.js'
import { logDebug, logInfo, logWarn } from '../../logging/index.js'
import { errorOrUndefined } from '../../utils/errorOrUndefined.js'
import { toMiddleware, toRequestHandler } from '../../utils/express.js'
import { fromCallback } from '../../utils/promise-utils.js'
import { login, logout } from '../index.js'
import { Sessions } from '../session.js'
import {
  createLogoutToken,
  injectLoginErrorToUrl,
  parseRelayState
} from './common.js'
import { parseDescriptionFromSamlError } from './error-utils.js'

const urlencodedParser = urlencoded({ extended: false })

type Role = 'employee' | 'citizen'

export function getRedirectUrl(type: Role, req: express.Request): string {
  return parseRelayState(req) ?? getRootUrl(type)
}

export function getRootUrl(type: Role): string {
  return type === 'employee' ? employeeRootUrl : citizenRootUrl
}

export interface SamlEndpointConfig {
  sessions: Sessions
  strategyName: string
  type: Role
  strategy: passportSaml.Strategy
}

const defaultNoAuthUrl = '/'

function createLoginHandler({
  sessions,
  strategyName,
  type
}: SamlEndpointConfig): express.RequestHandler {
  return (req, res, next) => {
    logInfo('Login endpoint called', req)
    const options: AuthenticateOptions = {}
    passport.authenticate(
      strategyName,
      options,
      (
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        err: any,
        user: (Express.User & passportSaml.Profile) | undefined
      ) => {
        if (err || !user) {
          const description =
            parseDescriptionFromSamlError(err, req) ||
            'Could not parse SAML message'

          if (err.message === 'InResponseTo is not valid' && req.user) {
            // When user uses browse back functionality after login we get invalid InResponseTo
            // This will ignore the error
            const redirectUrl = injectLoginErrorToUrl(getRedirectUrl(type, req))
            logDebug(`Redirecting to ${redirectUrl}`, req, { redirectUrl })
            return res.redirect(redirectUrl)
          }
          if (isUserInitiatedLoginFailure(err)) {
            const redirectUrl = injectLoginErrorToUrl(getRedirectUrl(type, req))
            logDebug(`Redirecting to ${redirectUrl}`, req, { redirectUrl })
            return res.redirect(redirectUrl)
          }

          logInfo(
            `Failed to authenticate user. Description: ${description}. Details: ${err}`,
            req
          )
          return res.redirect(defaultNoAuthUrl)
        }
        ;(async () => {
          await login(req, user)
          logInfo('User logged in successfully', req)

          if (!user.nameID) {
            throw new Error('User unexpectedly missing nameID property')
          }

          // Persist in session to allow custom logic per strategy
          req.session.idpProvider = strategyName
          await sessions.saveLogoutToken(
            req,
            createLogoutToken(user.nameID, user.sessionIndex)
          )

          const redirectUrl = getRedirectUrl(type, req)
          logDebug(`Redirecting to ${redirectUrl}`, req, { redirectUrl })
          return res.redirect(redirectUrl)
        })().catch((err) => {
          logInfo(`Error logging user in. Error: ${err}`, req)
          if (!res.headersSent) {
            res.redirect(defaultNoAuthUrl)
          } else {
            next(err)
          }
        })
      }
    )(req, res, next)
  }
}

function createLogoutHandler({
  sessions,
  strategy
}: SamlEndpointConfig): express.RequestHandler {
  return toRequestHandler(async (req, res) => {
    logInfo('Logout endpoint called', req)
    try {
      const redirectUrl = await fromCallback<string | null>((cb) =>
        req.user
          ? strategy.logout(req as unknown as RequestWithUser, cb)
          : cb(null, null)
      )

      logDebug('Logging user out from passport.', req)
      await logout(sessions, req, res)
      res.redirect(redirectUrl ?? defaultNoAuthUrl)
    } catch (err) {
      logInfo(
        `Log out failed. Description: Failed before redirecting user to IdP. Error: ${err}.`,
        req
      )
      throw err
    }
  })
}

// Configures passport to use the given strategy, and returns an Express router
// for handling SAML-related requests.
//
// We support two SAML "bindings", which define how data is passed by the
// browser to the SP (us) and the IDP.
// * HTTP redirect: the browser makes a GET request with query parameters
// * HTTP POST: the browser makes a POST request with URI-encoded form body
export default function createSamlRouter(
  endpointConfig: SamlEndpointConfig
): Router {
  const { strategyName, strategy } = endpointConfig

  passport.use(strategyName, strategy)

  const loginHandler = createLoginHandler(endpointConfig)
  const logoutHandler = createLogoutHandler(endpointConfig)
  const logoutCallback = toMiddleware(async (req) => {
    logInfo('Logout callback called', req)
  })
  const logoutErrorHandler: ErrorRequestHandler = (error, req, res, _next) => {
    logWarn('Logout failed', req, undefined, errorOrUndefined(error))
    if (!res.headersSent) {
      res.redirect(getRedirectUrl(endpointConfig.type, req))
    }
  }

  const router = Router()

  // Our application directs the browser to this endpoint to start the login
  // flow. We generate a LoginRequest.
  router.get(`/login`, loginHandler)
  // The IDP makes the browser POST to this callback during login flow, and
  // a SAML LoginResponse is included in the request.
  router.post(`/login/callback`, urlencodedParser, loginHandler)

  // Our application directs the browser to one of these endpoints to start
  // the logout flow. We generate a LogoutRequest.
  router.get(`/logout`, logoutHandler)
  // The IDP makes the browser either GET or POST one of these endpoints in two
  // separate logout flows.
  // 1. SP-initiated logout. In this case the logout flow started from us
  //   (= /auth/saml/logout endpoint), and a SAML LogoutResponse is included
  //   in the request.
  // 2. IDP-initiated logout (= SAML single logout). In this case the logout
  //   flow started from the IDP, and a SAML LogoutRequest is included in the
  //   request.
  router.get(
    `/logout/callback`,
    logoutCallback,
    passport.authenticate(strategyName),
    (req, res) => res.redirect(getRedirectUrl(endpointConfig.type, req))
  )
  router.post(
    `/logout/callback`,
    urlencodedParser,
    logoutCallback,
    passport.authenticate(strategyName),
    (req, res) => res.redirect(getRedirectUrl(endpointConfig.type, req))
  )
  router.use('/logout', logoutErrorHandler)

  router.use('/*splat', (_req, res) => {
    res.redirect(getRootUrl(endpointConfig.type))
  })

  return router
}

function isUserInitiatedLoginFailure(error?: { xmlStatus?: string }): boolean {
  const xmlStatus = error?.xmlStatus
  if (typeof xmlStatus !== 'string') {
    return false
  }

  return xmlStatus.includes('Requester') && xmlStatus.includes('AuthnFailed')
}
