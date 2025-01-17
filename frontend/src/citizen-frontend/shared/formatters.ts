import Decimal from 'decimal.js'

export function formatPlaceIdentifier(
  section: string,
  number: number,
  location?: string | null
): string {
  const locationPart = location ? `${location} ` : ''
  return `${locationPart}${section} ${String(number).padStart(3, '0')}`
}

export function formatDimensions({
  width,
  length
}: {
  width: number
  length: number
}) {
  return `${formatNumber(width, 2)} m x ${formatNumber(length, 2)} m`
}

export function formatNumber(
  value?: number | string,
  precision?: number
): string {
  const useDecimal = precision != undefined && precision > 0
  if (typeof value === 'string') {
    value = useDecimal ? parseFloat(value) : parseInt(value, 10)
  }
  return value !== undefined
    ? value.toFixed(precision ?? 0).replace('.', ',')
    : ''
}

export function formatPrice(value: number): string {
  return value.toFixed(2).replace('.', ',')
}

export function formatCmToM(value: number): number {
  return value / 100
}

export function formatMToCm(value: number): number {
  return value * 100
}

export function formatMToString(value: number): string {
  return value.toFixed(2).replace('.', ',')
}

export function formatCentsToEuros(cents: number): string {
  const decimals = new Decimal(cents)
  const euros = decimals.dividedBy(100).toFixed(2)
  return euros.replace('.', ',')
}
