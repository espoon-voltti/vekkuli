import { JsonOf } from '../../lib-common/json'
import { client } from '../api-client'
import { AuthStatus } from '../api-types/auth'

export async function getAuthStatus(): Promise<AuthStatus> {
  return (await client.get<JsonOf<AuthStatus>>('/public/current')).data
}
