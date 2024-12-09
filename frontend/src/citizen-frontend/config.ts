export const getLoginUri = (
  url = `${window.location.pathname}${window.location.search}${window.location.hash}`
) => `/api/auth/saml-suomifi/login?RelayState=${encodeURIComponent(url)}`

export const getLogoutUri = () => `/api/auth/saml-suomifi/logout`

export const getCreateReservationUri = () => `/api/reservations/create`
