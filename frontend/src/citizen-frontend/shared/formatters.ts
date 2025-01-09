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
  return `${formatNumber(width)} m x ${formatNumber(length)} m`
}

export function formatNumber(value?: number | string): string {
  if (typeof value === 'string') {
    value = parseFloat(value)
  }
  return value !== undefined ? value.toFixed(2).replace('.', ',') : ''
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
