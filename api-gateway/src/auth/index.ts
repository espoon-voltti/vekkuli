import express, { NextFunction, Request, Response } from 'express'
import jwt from 'jsonwebtoken'
import { readFileSync } from 'node:fs'
import passport, { AuthenticateCallback } from 'passport'
import { jwtKid, jwtPrivateKey } from '../config.js'
import { logInfo } from '../logging/index.js'
import { fromCallback } from '../utils/promise-utils.js'
import { Sessions } from './session.js'

export function requireAuthentication(userType: UserType) {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user || !req.user.id || req.user.type !== userType) {
      logInfo('Could not find user', req)
      if (req.path !== '/virkailija') {
        res.redirect('/')
        return
      }
    }
    return next()
  }
}

export type UserType = 'user' | 'citizen'

export interface AppSessionUser {
  id?: string | undefined
  type?: UserType
}

const jwtPrivateKeyValue = readFileSync(jwtPrivateKey)

export function createAuthHeader(user: AppSessionUser): string {
  if (!user.id) {
    throw new Error('User is missing an id')
  }

  const jwtPayload = {
    sub: user.id,
    type: user.type
  }
  const jwtToken = jwt.sign(jwtPayload, jwtPrivateKeyValue, {
    algorithm: 'RS256',
    expiresIn: '48h',
    keyid: jwtKid
  })
  return `Bearer ${jwtToken}`
}

export const authenticate = async (
  strategyName: string,
  req: express.Request,
  res: express.Response
): Promise<Express.User | undefined> =>
  await new Promise<Express.User | undefined>((resolve, reject) => {
    const cb: AuthenticateCallback = (err, user) =>
      err ? reject(err) : resolve(user || undefined)
    const next: express.NextFunction = (err) =>
      err ? reject(err) : resolve(undefined)
    passport.authenticate(strategyName, cb)(req, res, next)
  })

export const login = async (
  req: express.Request,
  user: Express.User
): Promise<void> => {
  await fromCallback<void>((cb) => req.logIn(user, cb))
  // Passport has now regenerated the active session and saved it, so we have a
  // guarantee that the session ID has changed and Redis has stored the new session data
}

export const logout = async (
  sessions: Sessions,
  req: express.Request,
  res: express.Response
): Promise<void> => {
  // Pre-emptively clear the cookie, so even if something fails later, we
  // will end up clearing the cookie in the response
  res.clearCookie(sessions.cookieName)

  const logoutToken = req.session?.logoutToken?.value

  await fromCallback<void>((cb) => req.logOut(cb))
  // Passport has now saved the previous session with null user and regenerated
  // the active session, so we have a guarantee that the ID has changed and
  // the old session data in Redis no longer includes the user

  if (logoutToken) {
    await sessions.consumeLogoutToken(logoutToken)
  }
  await fromCallback((cb) =>
    req.session ? req.session.destroy(cb) : cb(undefined)
  )
}
