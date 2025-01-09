import cookieParser from 'cookie-parser'
import { Request, Response, Router } from 'express'
import passport from 'passport'
import { sessionSupport } from './auth/session.js'
import { RedisClient } from './clients/redis-client.js'
import { appCommit, Config } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'

export function createSystemRouter(
  config: Config,
  redisClient: RedisClient
): Router {
  const router = Router()

  const sessions = sessionSupport(redisClient, config.session)

  router.use(sessions.middleware)
  router.use(passport.session())
  router.use(cookieParser(config.session.cookieSecret))

  router.use(cacheControl(() => 'forbid-cache'))

  router.all(
    '/system/*splat',
    // TODO fix the any. This was added to make the build pass
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (_: Request, res: Response) => res.sendStatus(404) as any
  )

  router.get('/version', (_, res) => {
    res.send({ commitId: appCommit })
  })

  router.use(errorHandler)

  return router
}
