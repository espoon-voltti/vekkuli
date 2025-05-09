import cookieParser from 'cookie-parser'
import { Router } from 'express'
import passport from 'passport'
import { createAdSamlStrategy } from './auth/ad-saml.js'
import { createDevAdRouter } from './auth/dev-ad-auth.js'
import { createDevSfiRouter } from './auth/dev-sfi-auth.js'
import { createSamlConfig } from './auth/saml/common.js'
import redisCacheProvider from './auth/saml/passport-saml-cache-redis.js'
import createSamlRouter from './auth/saml/saml-routes.js'
import { sessionSupport } from './auth/session.js'
import { createSuomiFiStrategy } from './auth/suomifi-saml.js'
import { citizenRootUrl, Config } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'
import { VekkuliRedisClient } from './index.js'

export function createAuthRouter(
  config: Config,
  redisClient: VekkuliRedisClient
): Router {
  const router = Router()

  const citizenSessions = sessionSupport(
    'citizen',
    redisClient,
    config.citizenSession
  )
  router.use(
    '/saml-suomifi',
    citizenSessions.middleware,
    passport.session(),
    cookieParser(config.citizenSession.cookieSecret)
  )

  const userSessions = sessionSupport('user', redisClient, config.userSession)
  router.use(
    '/saml',
    userSessions.middleware,
    passport.session(),
    cookieParser(config.userSession.cookieSecret)
  )

  router.use(cacheControl(() => 'forbid-cache'))

  if (config.sfi.type === 'mock') {
    router.use('/saml-suomifi', createDevSfiRouter(citizenSessions))
  } else {
    router.use(
      '/saml-suomifi',
      createSamlRouter({
        sessions: citizenSessions,
        strategyName: 'suomifi',
        strategy: createSuomiFiStrategy(
          citizenSessions,
          createSamlConfig(
            config.sfi.saml,
            redisCacheProvider(redisClient, {
              keyPrefix: 'suomifi-saml-resp:'
            })
          )
        ),
        type: 'citizen'
      })
    )
  }

  if (config.ad.type === 'mock') {
    router.use('/saml', createDevAdRouter(userSessions))
  } else if (config.ad.type === 'saml') {
    router.use(
      '/saml',
      createSamlRouter({
        sessions: userSessions,
        strategyName: 'ead',
        strategy: createAdSamlStrategy(
          userSessions,
          config.ad,
          createSamlConfig(
            config.ad.saml,
            redisCacheProvider(redisClient, {
              keyPrefix: 'ad-saml-resp:'
            })
          )
        ),
        type: 'employee'
      })
    )
  }

  router.use('/*splat', (_req, res) => {
    res.redirect(citizenRootUrl)
  })

  router.use(errorHandler)

  return router
}
