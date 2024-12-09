export function formatPlaceIdentifier(section: string, number: number): string {
  return `${section} ${String(number).padStart(3, '0')}`
}

export function formatDimensions({
  width,
  length
}: {
  width: number
  length: number
}) {
  return `${formatDimension(width)} x ${formatDimension(length)} m`
}

export function formatDimension(value: number): string {
  return value.toFixed(1).replace('.', ',')
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
