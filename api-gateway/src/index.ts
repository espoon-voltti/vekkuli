import './tracer.js'

import express from 'express'
import helmet from 'helmet'
import passport from 'passport'
import { createClient } from 'redis'
import sourceMapSupport from 'source-map-support'
import { createAuthRouter } from './authRouter.js'
import { createCitizenRouter } from './citizenRouter.js'
import { configFromEnv, httpPort } from './config.js'
import { createEmployeeRouter } from './employeeRouter.js'
import { logError, loggingMiddleware } from './logging/index.js'
import { fallbackErrorHandler } from './middleware/errors.js'
import { createSystemRouter } from './systemRouter.js'
import { trustReverseProxy } from './utils/express.js'
import { createDevRouter } from './devRouter.js'

sourceMapSupport.install()
const config = configFromEnv()

const socketOptions = config.redis.disableSecurity
  ? {
      host: config.redis.host!,
      port: config.redis.port!
    }
  : {
      host: config.redis.host!,
      port: config.redis.port!,
      tls: true as const,
      servername: config.redis.tlsServerName!
    }

const redisClient = createClient({
  socket: socketOptions,
  ...(config.redis.disableSecurity ? {} : { password: config.redis.password })
})

export type VekkuliRedisClient = typeof redisClient

redisClient.on('error', (err) =>
  logError('Redis error', undefined, undefined, err)
)
redisClient.connect().catch((err) => {
  logError('Unable to connect to redis', undefined, undefined, err)
})
// Don't prevent the app from exiting if a redis connection is alive.
redisClient.unref()

const app = express()
trustReverseProxy(app)
app.set('etag', false)

app.use(
  helmet({
    // Content-Security-Policy is set by the nginx proxy
    contentSecurityPolicy: false
  })
)
app.get('/health', (_, res) => {
  if (!redisClient.isReady) {
    throw new Error('not connected to redis')
  }
  redisClient
    .ping()
    .then(() => {
      res.status(200).json({ status: 'UP' })
    })
    .catch(() => {
      res.status(503).json({ status: 'DOWN' })
    })
})
app.use(loggingMiddleware)

passport.serializeUser<Express.User>((user, done) => done(null, user))
passport.deserializeUser<Express.User>((user, done) => done(null, user))

app.use('/auth', createAuthRouter(config, redisClient))
app.use('/api/citizen', createCitizenRouter(config, redisClient))
app.use('/dev', createDevRouter(config, redisClient))
app.use('/', createSystemRouter())
app.use('/', createEmployeeRouter(config, redisClient))
app.use(fallbackErrorHandler)

const server = app.listen(httpPort, () => {
  console.log(`Vekkuli API Gateway listening on port ${httpPort}`)
})

server.keepAliveTimeout = 70 * 1000
server.headersTimeout = 75 * 1000
