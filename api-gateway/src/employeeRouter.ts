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

  router.get('/static/*splat', proxy)
  router.get('/kuntalainen/venepaikat/*splat', proxy)
  router.get('/kuntalainen/venepaikat', proxy)
  router.get('/kuntalainen/partial/vapaat-paikat', proxy)
  router.get('/ext/*splat', proxy)
  router.get('/', proxy)

  router.use(requireAuthentication)
  router.use(proxy)
  router.use(errorHandler)

  return router
}
