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
  precision?: number,
  separator = ','
): string {
  const useDecimal = precision != undefined && precision > 0
  if (typeof value === 'string') {
    value = useDecimal ? parseFloat(value) : parseInt(value, 10)
  }
  return value !== undefined
    ? value.toFixed(precision ?? 0).replace('.', separator)
    : ''
}

export function formatPrice(value: number): string {
  return value.toFixed(2).replace('.', ',')
}

export function parsePrice(price: string): number {
  return parseFloat(price.replace(',', '.'))
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
  const euros: number = Math.round((cents / 100) * 100) / 100
  return euros.toFixed(2).replace('.', ',')
}
