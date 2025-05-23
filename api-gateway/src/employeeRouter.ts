import cookieParser from 'cookie-parser'
import { Router } from 'express'
import passport from 'passport'
import { requireAuthentication } from './auth/index.js'
import { sessionSupport } from './auth/session.js'
import { Config, employeeRootUrl, serviceUrl } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'
import { createProxy } from './utils/proxy.js'
import { VekkuliRedisClient } from './index.js'

export function createEmployeeRouter(
  config: Config,
  redisClient: VekkuliRedisClient
): Router {
  const router = Router()

  const sessions = sessionSupport('user', redisClient, config.userSession)
  const proxy = createProxy(serviceUrl)

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.userSession.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  router.get('/virkailija/static/*splat', proxy)
  router.get('/ext/*splat', proxy)

  router.use(
    requireAuthentication('user', (req, res, next) => {
      if (req.path !== employeeRootUrl) {
        res.redirect(employeeRootUrl)
      } else {
        next()
      }
    })
  )
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
