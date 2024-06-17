import type { CacheProvider } from '@node-saml/passport-saml'
import { RedisClient } from '../../clients/redis-client.js'

export interface ProviderOptions {
  /**
   * Entries older than this are deleted automatically by Redis' expire mechanism
   */
  ttlSeconds?: number
  /**
   * Prefix for cache item keys in Redis.
   * If creating multiple passport-saml strategy instances targeting the same
   * Redis database, you should set a unique keyPrefix per strategy.
   */
  keyPrefix: string
}

/**
 * Custom passport-saml CacheProvider for Redis.
 *
 * This allows the use of validateInResponseTo in multi-instance environments
 * where the instances obviously cannot share access to the same
 * InMemoryCacheProvider. Instead, a shared Redis cache is used.
 *
 * This cache provider also supports using it with multiple passport-saml
 * Strategies simultaneously by allowing keyPrefix configuration.
 */
export default function redisCacheProvider(
  client: RedisClient,
  options: ProviderOptions
): CacheProvider {
  const { ttlSeconds = 60 * 60, keyPrefix } = options

  if (!Number.isInteger(ttlSeconds) || ttlSeconds <= 0) {
    throw new Error('ttlSeconds must be a positive integer')
  }

  return {
    getAsync: (key: string) => client.get(`${keyPrefix}${key}`),
    saveAsync: async (key: string, value: string) => {
      const reply = await client.get(`${keyPrefix}${key}`)
      if (reply !== null) return null
      await client.set(`${keyPrefix}${key}`, value, { EX: ttlSeconds })
      return { createdAt: new Date().getTime(), value }
    },
    removeAsync: async (key: string) => {
      const count = await client.del(`${keyPrefix}${key}`)
      if (count === 0) return null
      return key
    }
  }
}
