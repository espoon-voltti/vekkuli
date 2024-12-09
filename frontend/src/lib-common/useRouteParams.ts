import { useParams } from 'react-router'

export default function useRouteParams<
  const Required extends string[],
  const Optional extends string[]
>(
  required: Required,
  optional?: Optional
): RequiredObject<Required> & OptionalObject<Optional> {
  const params = useParams()
  return Object.fromEntries([
    ...required.map((key) => {
      const value = params[key]
      return [key, value]
    }),
    ...(optional || []).flatMap((key) => {
      const value = params[key]
      if (value === undefined) {
        return []
      } else {
        return [[key, value]]
      }
    })
  ]) as RequiredObject<Required> & OptionalObject<Optional>
}

type RequiredObject<T extends string[]> = Record<T[number], string>
type OptionalObject<T extends string[]> = Partial<Record<T[number], string>>
