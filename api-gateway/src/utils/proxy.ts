import expressHttpProxy from 'express-http-proxy'
import { createServiceRequestHeaders } from '../clients/service-client.js'

export function createProxy(serviceUrl: string) {
  return expressHttpProxy(serviceUrl, {
    parseReqBody: false,
    proxyReqPathResolver: (req) => req.originalUrl,
    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
      const headers = createServiceRequestHeaders(srcReq)
      proxyReqOpts.headers = {
        ...proxyReqOpts.headers,
        ...headers
      }
      return proxyReqOpts
    }
  })
}
