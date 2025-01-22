import { Municipality } from 'citizen-frontend/api-types/reservation'

export function getMunicipalityName(
  code: number | undefined,
  municipalities: Municipality[]
): string {
  if (!code) return '-'
  const municipality = municipalities.find((m) => m.code === code)
  return municipality ? municipality.name : '-'
}
