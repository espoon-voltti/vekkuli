export const getLoginUri = (
  url = `${window.location.pathname}${window.location.search}${window.location.hash}`
) =>
  `/auth/saml-suomifi/login?RelayState=${encodeURIComponent(removeLoginErrorParameterFromUrl(url))}`

export const getLogoutUri = () => `/auth/saml-suomifi/logout`

function removeLoginErrorParameterFromUrl(url: string): string {
  const isAbsoluteUrl = url.startsWith('http')
  const dummyBase = 'http://dummy'
  const target = isAbsoluteUrl ? new URL(url) : new URL(url, dummyBase)
  target.searchParams.delete('loginError')
  return isAbsoluteUrl
    ? target.href
    : `${target.pathname}${target.search}${target.hash}`
}
