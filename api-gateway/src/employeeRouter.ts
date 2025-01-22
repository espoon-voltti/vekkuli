import cookieParser from 'cookie-parser'
import { Router } from 'express'
import passport from 'passport'
import { requireAuthentication } from './auth/index.js'
import { sessionSupport } from './auth/session.js'
import { RedisClient } from './clients/redis-client.js'
import { Config, serviceUrl } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'
import { createProxy } from './utils/proxy.js'

export function createEmployeeRouter(
  config: Config,
  redisClient: RedisClient
): Router {
  const router = Router()

  const sessions = sessionSupport(redisClient, config.session)
  const proxy = createProxy(serviceUrl)

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.session.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  router.get('/virkailija/static/*splat', proxy)
  router.get('/ext/*splat', proxy)

  router.use(requireAuthentication('user'))
  router.use('/admin', proxy)
  router.use('/boat-space', proxy)
  router.use('/reservation', proxy)
  router.use('/validate', proxy)
  router.use('/info', proxy)
  router.use('/venepaikka', proxy)
  router.use('/virkailija', proxy)
  router.use('/yhteiso', proxy)
  router.use(errorHandler)

  return router
}
