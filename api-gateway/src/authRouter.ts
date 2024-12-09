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
import { RedisClient } from './clients/redis-client.js'
import { Config } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'

export function createAuthRouter(
  config: Config,
  redisClient: RedisClient
): Router {
  const router = Router()

  const sessions = sessionSupport(redisClient, config.session)

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.session.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  if (config.sfi.type === 'mock') {
    router.use('/saml-suomifi', createDevSfiRouter(sessions))
  } else {
    router.use(
      '/saml-suomifi',
      createSamlRouter({
        sessions,
        strategyName: 'suomifi',
        strategy: createSuomiFiStrategy(
          sessions,
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
    router.use('/saml', createDevAdRouter(sessions))
  } else if (config.ad.type === 'saml') {
    router.use(
      '/saml',
      createSamlRouter({
        sessions,
        strategyName: 'ead',
        strategy: createAdSamlStrategy(
          sessions,
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

  router.use(errorHandler)

  return router
}
