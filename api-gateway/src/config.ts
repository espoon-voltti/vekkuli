import { ValidateInResponseTo } from '@node-saml/node-saml'

export interface Config {
  citizenSession: SessionConfig
  userSession: SessionConfig
  ad: {
    externalIdPrefix: string
    userIdKey: string
  } & AdConfig
  sfi: AdConfig
  redis: RedisConfig
}

export interface SessionConfig {
  useSecureCookies: boolean
  cookieSecret: string
  sessionTimeoutMinutes: number
}

type AdConfig = MockAdConfig | EspooAdConfig

interface MockAdConfig {
  type: 'mock'
}

interface EspooAdConfig {
  type: 'saml'
  saml: EspooSamlConfig
}

interface RedisConfig {
  host: string | undefined
  port: number | undefined
  password: string | undefined
  tlsServerName: string | undefined
  disableSecurity: boolean
}

export interface EspooSamlConfig {
  callbackUrl: string
  entryPoint: string
  logoutUrl: string
  issuer: string
  publicCert: string | string[]
  privateCert: string
  validateInResponseTo: ValidateInResponseTo
  decryptAssertions: boolean
  nameIdFormat?: string | undefined
}

export const nodeEnvs = ['local', 'test', 'production'] as const
export type NodeEnv = (typeof nodeEnvs)[number]

function ifNodeEnv<T>(envs: NodeEnv[], value: T): T | undefined {
  return envs.some((env) => process.env.NODE_ENV === env) ? value : undefined
}

function required<T>(value: T | undefined): T {
  if (value === undefined) {
    throw new Error('Configuration parameter was not provided')
  }
  return value
}

function parseInteger(value: string): number {
  const result = Number.parseInt(value, 10)
  if (Number.isNaN(result)) throw new Error('Invalid integer')
  return result
}

const booleans: Record<string, boolean> = {
  1: true,
  0: false,
  true: true,
  false: false
}

function parseBoolean(value: string): boolean {
  if (value in booleans) return booleans[value]
  throw new Error('Invalid boolean')
}

function env<T>(key: string, parser: (value: string) => T): T | undefined {
  const value = process.env[key]
  if (value === undefined || value === '') return undefined
  try {
    return parser(value)
  } catch (err) {
    const message = err instanceof Error ? err.message : String(err)
    throw new Error(`${message}: ${key}=${value}`)
  }
}

function envArray<T>(
  key: string,
  parser: (value: string) => T,
  separator = ','
): T[] | undefined {
  const value = process.env[key]
  if (value === undefined || value === '') return undefined
  const values = value.split(separator)
  try {
    return values.map(parser)
  } catch (err) {
    const message = err instanceof Error ? err.message : String(err)
    throw new Error(`${message}: ${key}=${value}`)
  }
}

export function configFromEnv(): Config {
  const adMock =
    env('AD_MOCK', parseBoolean) ??
    env('DEV_LOGIN', parseBoolean) ??
    ifNodeEnv(['local', 'test'], true) ??
    false

  const adType = adMock ? 'mock' : 'saml'

  const defaultUserIdKey =
    'http://schemas.microsoft.com/identity/claims/objectidentifier'

  const ad: Config['ad'] = {
    externalIdPrefix: process.env.AD_SAML_EXTERNAL_ID_PREFIX ?? 'espoo-ad',
    userIdKey: process.env.AD_USER_ID_KEY ?? defaultUserIdKey,
    ...(adType !== 'saml'
      ? { type: adType }
      : {
          type: adType,
          saml: {
            callbackUrl: required(process.env.AD_SAML_CALLBACK_URL),
            entryPoint: required(process.env.AD_SAML_ENTRYPOINT_URL),
            logoutUrl: required(process.env.AD_SAML_LOGOUT_URL),
            issuer: required(process.env.AD_SAML_ISSUER),
            publicCert: required(
              envArray('AD_SAML_PUBLIC_CERT', (value) => value)
            ),
            privateCert: required(process.env.AD_SAML_PRIVATE_CERT),
            validateInResponseTo: ValidateInResponseTo.always,
            decryptAssertions:
              env('AD_DECRYPT_ASSERTIONS', parseBoolean) ?? false,
            nameIdFormat: process.env.AD_NAME_ID_FORMAT
          }
        })
  }

  const sfi: Config['sfi'] = {
    ...(adType !== 'saml'
      ? { type: adType }
      : {
          type: adType,
          saml: {
            callbackUrl: required(process.env.SFI_SAML_CALLBACK_URL),
            entryPoint: required(process.env.SFI_SAML_ENTRYPOINT),
            logoutUrl: required(process.env.SFI_SAML_LOGOUT_URL),
            issuer: required(process.env.SFI_SAML_ISSUER),
            publicCert: required(
              envArray('SFI_SAML_PUBLIC_CERT', (value) => value)
            ),
            privateCert: required(process.env.SFI_SAML_PRIVATE_CERT),
            validateInResponseTo: ValidateInResponseTo.always,
            decryptAssertions:
              env('SFI_DECRYPT_ASSERTIONS', parseBoolean) ?? false
          }
        })
  }

  const citizenCookieSecret = required(
    process.env.CITIZEN_COOKIE_SECRET ??
      ifNodeEnv(['local', 'test'], 'A very hush hush citizen cookie secret.')
  )

  const userCookieSecret = required(
    process.env.USER_COOKIE_SECRET ??
      ifNodeEnv(['local', 'test'], 'A very hush hush user cookie secret.')
  )

  const useSecureCookies =
    env('USE_SECURE_COOKIES', parseBoolean) ??
    ifNodeEnv(['local', 'test'], false) ??
    true

  return {
    citizenSession: {
      useSecureCookies,
      cookieSecret: citizenCookieSecret,
      sessionTimeoutMinutes:
        env('CITIZEN_SESSION_TIMEOUT_MINUTES', parseInteger) ?? 8 * 60
    },
    userSession: {
      useSecureCookies,
      cookieSecret: userCookieSecret,
      sessionTimeoutMinutes:
        env('USER_SESSION_TIMEOUT_MINUTES', parseInteger) ?? 8 * 60
    },
    ad,
    sfi,
    redis: {
      host: process.env.REDIS_HOST ?? ifNodeEnv(['local'], '127.0.0.1'),
      port: env('REDIS_PORT', parseInteger) ?? ifNodeEnv(['local'], 6379),
      password: process.env.REDIS_PASSWORD,
      tlsServerName: process.env.REDIS_TLS_SERVER_NAME,
      disableSecurity:
        env('REDIS_DISABLE_SECURITY', parseBoolean) ??
        ifNodeEnv(['local'], true) ??
        false
    }
  }
}

export const appBuild = process.env.APP_BUILD ?? 'UNDEFINED'
export const appCommit = process.env.APP_COMMIT ?? 'UNDEFINED'
export const hostIp = process.env.HOST_IP ?? 'UNDEFINED'

export const tracingEnabled = parseBoolean(
  process.env.DD_TRACE_ENABLED || 'false'
)
export const profilingEnabled = parseBoolean(
  process.env.DD_PROFILING_ENABLED || 'false'
)
export const traceAgentHostname =
  process.env.DD_TRACE_AGENT_HOSTNAME || 'localhost'
export const traceAgentPort = parseInteger(
  process.env.DD_TRACE_AGENT_PORT || '8126'
)

export const jwtPrivateKey = required(
  process.env.JWT_PRIVATE_KEY ??
    ifNodeEnv(['local', 'test'], 'config/test-cert/jwt_private_key.pem')
)

export const appName = 'vekkuli-api-gateway'
export const jwtKid = process.env.JWT_KID ?? appName

export const appBaseUrl = required(
  process.env.BASE_URL ?? ifNodeEnv(['local', 'test'], 'local')
)

export const citizenRootUrl = '/'
export const employeeRootUrl = '/virkailija'

export const serviceUrl = required(
  process.env.SERVICE_URL ??
    ifNodeEnv(['local', 'test'], 'http://localhost:8080')
)
export const useSecureCookies =
  env('USE_SECURE_COOKIES', parseBoolean) ??
  ifNodeEnv(['local', 'test'], false) ??
  true

export const prettyLogs =
  env('PRETTY_LOGS', parseBoolean) ?? ifNodeEnv(['local'], true) ?? false

export const volttiEnv = process.env.VOLTTI_ENV ?? ifNodeEnv(['local'], 'local')

export const httpPort = env('HTTP_PORT', parseInteger) ?? 3000

export const csrfCookieName = 'XSRF-TOKEN'
