export function errorOrUndefined(error: unknown): Error | undefined {
  return error instanceof Error ? error : undefined
}
