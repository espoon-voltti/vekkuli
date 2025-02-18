import cookieParser from 'cookie-parser'
import { Router } from 'express'
import passport from 'passport'
import { requireAuthentication } from './auth/index.js'
import { sessionSupport } from './auth/session.js'
import { RedisClient } from './clients/redis-client.js'
import { Config, employeeRootUrl, serviceUrl } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'
import { createProxy } from './utils/proxy.js'

export function createDevRouter(
  config: Config,
  redisClient: RedisClient
): Router {
  const router = Router()

  const sessions = sessionSupport('user', redisClient, config.userSession)
  const proxy = createProxy(serviceUrl)

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.userSession.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  router.use(
    requireAuthentication('user', (_req, res) => {
      res.redirect(employeeRootUrl)
    })
  )
  router.use(proxy)
  router.use(errorHandler)

  return router
}
