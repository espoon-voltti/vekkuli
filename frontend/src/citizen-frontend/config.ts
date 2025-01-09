export const getLoginUri = (
  url = `${window.location.pathname}${window.location.search}${window.location.hash}`
) => `/auth/saml-suomifi/login?RelayState=${encodeURIComponent(url)}`

export const getLogoutUri = () => `/auth/saml-suomifi/logout`
