import { Router } from 'express'
import expressHttpProxy from 'express-http-proxy'

import { errorHandler } from './middleware/errors.js'
import { appCommit, Config, serviceUrl } from './config.js'
import { RedisClient } from './clients/redis-client.js'
import { sessionSupport } from './auth/session.js'
import passport from 'passport'
import cookieParser from 'cookie-parser'
import { cacheControl } from './middleware/cache-control.js'
import { requireAuthentication } from './auth/index.js'
import { createDevAdRouter } from './auth/dev-ad-auth.js'
import { createServiceRequestHeaders } from './clients/service-client.js'
import createSamlRouter from './auth/saml/saml-routes.js'
import { createAdSamlStrategy } from './auth/ad-saml.js'
import redisCacheProvider from './auth/saml/passport-saml-cache-redis.js'
import { createDevSfiRouter } from './auth/dev-sfi-auth.js'
import { createSamlConfig } from './auth/saml/common.js'
import { createSuomiFiStrategy } from './auth/suomifi-saml.js'

export function createRouter(config: Config, redisClient: RedisClient): Router {
  const router = Router()

  const sessions = sessionSupport(redisClient, config.session)
  const proxy = expressHttpProxy(serviceUrl, {
    parseReqBody: false,
    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
      const headers = createServiceRequestHeaders(srcReq)
      proxyReqOpts.headers = {
        ...proxyReqOpts.headers,
        ...headers
      }
      return proxyReqOpts
    }
  })

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.session.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  router.get('/', proxy)

  router.all('/system/*', (_, res) => res.sendStatus(404))

  if (config.sfi.type === 'mock') {
    router.use('/auth/saml-suomifi', createDevSfiRouter(sessions))
  } else {
    router.use(
      '/auth/saml-suomifi',
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
    router.use('/auth/saml', createDevAdRouter(sessions))
  } else if (config.ad.type === 'saml') {
    router.use(
      '/auth/saml',
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

  router.get('/version', (_, res) => {
    res.send({ commitId: appCommit })
  })
  router.use(requireAuthentication)
  router.use(proxy)
  router.use(errorHandler)

  return router
}
