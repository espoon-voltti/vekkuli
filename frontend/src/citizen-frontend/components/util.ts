import { MutationDescription } from 'lib-common/query'

export const createMutationDisabledDefault = <
  TArg,
  TData
>(): MutationDescription<TArg, TData> =>
  ({ api: (_arg: TArg) => Promise.resolve() }) as MutationDescription<
    TArg,
    TData
  >
