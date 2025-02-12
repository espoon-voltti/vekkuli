import { Request, Response, Router } from 'express'
import { appCommit } from './config.js'
import { cacheControl } from './middleware/cache-control.js'
import { errorHandler } from './middleware/errors.js'

export function createSystemRouter(): Router {
  const router = Router()

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
