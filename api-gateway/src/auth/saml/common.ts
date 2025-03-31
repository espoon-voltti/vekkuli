// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { z } from 'zod'
import _ from 'lodash'
import {
  CacheProvider,
  Profile,
  SamlConfig,
  Strategy as SamlStrategy,
  VerifyWithRequest
} from '@node-saml/passport-saml'

import { appBaseUrl, EspooSamlConfig } from '../../config.js'
import { readFileSync } from 'node:fs'
import certificates, { TrustedCertificates } from './certificates.js'
import express from 'express'
import path from 'node:path'
import { Sessions } from '../session.js'
import { AppSessionUser } from '../index.js'
import { logInfo } from '../../logging/index.js'

export function fromCallback<T>(
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  f: (cb: (err: any, result?: T) => void) => void
): Promise<T> {
  return new Promise<T>((resolve, reject) =>
    f((err, result) => (err ? reject(err) : resolve(result!)))
  )
}

export function createSamlConfig(
  config: EspooSamlConfig,
  cacheProvider?: CacheProvider
): SamlConfig & { passReqToCallback: boolean } {
  const privateCert = readFileSync(config.privateCert, {
    encoding: 'utf8'
  })
  const lookupPublicCert = (cert: string) =>
    cert in certificates
      ? certificates[cert as TrustedCertificates]
      : readFileSync(cert, {
          encoding: 'utf8'
        })
  const publicCert = Array.isArray(config.publicCert)
    ? config.publicCert.map(lookupPublicCert)
    : lookupPublicCert(config.publicCert)

  return {
    acceptedClockSkewMs: 0,
    audience: config.issuer,
    cacheProvider,
    callbackUrl: config.callbackUrl,
    idpCert: publicCert,
    disableRequestedAuthnContext: true,
    decryptionPvk: config.decryptAssertions ? privateCert : undefined,
    entryPoint: config.entryPoint,
    identifierFormat:
      config.nameIdFormat ??
      'urn:oasis:names:tc:SAML:2.0:nameid-format:transient',
    issuer: config.issuer,
    logoutUrl: config.logoutUrl,
    privateKey: privateCert,
    signatureAlgorithm: 'sha256',
    validateInResponseTo: config.validateInResponseTo,
    passReqToCallback: true,
    // When *both* wantXXXXSigned settings are false, passport-saml still
    // requires at least the whole response *or* the assertion to be signed, so
    // these settings don't introduce a security problem
    wantAssertionsSigned: false,
    wantAuthnResponseSigned: false
  }
}

// A subset of SAML Profile fields that are expected to be present in Profile
// *and* req.user in valid SAML sessions
const SamlProfileId = z.object({
  nameID: z.string(),
  sessionIndex: z.string().optional()
})

export function createLogoutToken(
  nameID: Required<Profile>['nameID'],
  sessionIndex: Profile['sessionIndex']
) {
  return `${nameID}:::${sessionIndex}`
}

export function createSamlStrategy<T>(
  sessions: Sessions,
  config: SamlConfig,
  profileSchema: z.ZodType<T>,
  login: (profile: T) => Promise<AppSessionUser>
): SamlStrategy {
  const loginVerify: VerifyWithRequest = (req, profile, done) => {
    if (!profile) return done(null, undefined)
    logInfo('Data from SAML', undefined, { profile: profile })
    const parseResult = profileSchema.safeParse(profile)
    if (!parseResult.success) {
      return done(
        new Error(
          `SAML ${profile.issuer} profile parsing failed: ${parseResult.error.message}`
        )
      )
    }
    login(parseResult.data)
      .then((user) => {
        // Despite what the typings say, passport-saml assumes
        // we give it back a valid Profile, including at least some of these
        // SAML-specific fields
        const samlUser: AppSessionUser & Profile = {
          ...user,
          issuer: profile.issuer,
          nameID: profile.nameID,
          nameIDFormat: profile.nameIDFormat,
          nameQualifier: profile.nameQualifier,
          spNameQualifier: profile.spNameQualifier,
          sessionIndex: profile.sessionIndex
        }
        done(null, samlUser)
      })
      .catch(done)
  }
  const logoutVerify: VerifyWithRequest = (req, profile, done) =>
    (async () => {
      if (!profile) return undefined
      const profileId = SamlProfileId.safeParse(profile)
      if (!profileId.success) return undefined
      if (!req.user) {
        // We're possibly doing SLO without a real session (e.g. browser has
        // 3rd party cookies disabled). We need to retrieve the session data
        // and recreate req.user for this request
        const logoutToken = createLogoutToken(
          profile.nameID,
          profile.sessionIndex
        )
        const user = await sessions.logoutWithToken(logoutToken)
        if (user) {
          // Set req.user for *this request only*
          await fromCallback((cb) =>
            req.login(
              { ...user, type: 'user' },
              { session: false, keepSessionInfo: false },
              cb
            )
          )
        }
      }
      const reqUser: Partial<Profile> = (req.user ?? {}) as Partial<Profile>
      const reqId = SamlProfileId.safeParse(reqUser)
      if (reqId.success && _.isEqual(reqId.data, profileId.data)) {
        return reqUser
      }
    })()
      .then((user) => done(null, user))
      .catch((err) => done(err))
  return new SamlStrategy(config, loginVerify, logoutVerify)
}

export function parseRelayState(req: express.Request): string | undefined {
  const relayState = req.body?.RelayState || req.query.RelayState

  if (typeof relayState === 'string' && path.isAbsolute(relayState)) {
    if (appBaseUrl === 'local') {
      return relayState
    } else {
      const baseUrl = appBaseUrl.replace(/\/$/, '')
      const redirect = new URL(relayState, baseUrl)
      if (redirect.origin == baseUrl) {
        return redirect.href
      }
    }
  }

  // if (relayState) logError('Invalid RelayState in request', req)

  return undefined
}

export function injectLoginErrorToUrl(url: string): string {
  const isAbsoluteUrl = url.startsWith('http')
  const dummyBase = 'http://dummy'
  const target = isAbsoluteUrl ? new URL(url) : new URL(url, dummyBase)
  target.searchParams.set('loginError', 'true')
  return isAbsoluteUrl
    ? target.href
    : `${target.pathname}${target.search}${target.hash}`
}
