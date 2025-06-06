import cookieParser from 'cookie-parser'
import { Router } from 'express'
import passport from 'passport'
import { requireAuthentication } from './auth/index.js'
import { sessionSupport } from './auth/session.js'
import { Config, serviceUrl } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'
import { createProxy } from './utils/proxy.js'
import { VekkuliRedisClient } from './index.js'

export function createCitizenRouter(
  config: Config,
  redisClient: VekkuliRedisClient
): Router {
  const router = Router()

  const sessions = sessionSupport('citizen', redisClient, config.citizenSession)
  const proxy = createProxy(serviceUrl)

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.citizenSession.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  router.get('/public/*splat', proxy)

  router.use(
    requireAuthentication('citizen', (_req, res) => {
      res.status(401).json({ error: 'Unauthorized' })
    })
  )
  router.use(proxy)
  router.use(errorHandler)

  return router
}
