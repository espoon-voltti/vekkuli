import express from 'express'
import axios from 'axios'
import { AppSessionUser, createAuthHeader, UserType } from '../auth/index.js'
import { serviceUrl } from '../config.js'

export const client = axios.create({
  baseURL: serviceUrl
})

const systemUser: AppSessionUser = {
  id: '00000000-0000-0000-0000-000000000000',
  type: 'user'
}

export type ServiceRequestHeader = 'Authorization' | 'X-Request-ID'

export type ServiceRequestHeaders = { [H in ServiceRequestHeader]?: string }

export function createServiceRequestHeaders(
  req: express.Request | undefined,
  user: AppSessionUser | undefined | null = req?.user
) {
  const headers: ServiceRequestHeaders = {}
  if (user) {
    headers.Authorization = createAuthHeader(user)
  }
  return headers
}

export interface AdUser {
  externalId: string
  firstName: string
  lastName: string
  email?: string | null
}

export interface CitizenUser {
  address?: { sv: string; fi: string }
  email?: string
  firstName: string
  lastName: string
  nationalId: string
  postalCode?: string
  town?: { sv: string; fi: string }
  homeTown?: string
}

export async function userLogin(
  adUser: AdUser
): Promise<AdUser & { id: string; type: UserType }> {
  const res = await client.post<AdUser & { id: string; type: UserType }>(
    `/system/user-login`,
    adUser,
    {
      headers: createServiceRequestHeaders(undefined, systemUser)
    }
  )
  return { ...res.data, type: 'user' }
}

export async function citizenLogin(
  citizen: CitizenUser
): Promise<CitizenUser & { id: string; type: UserType }> {
  const res = await client.post<CitizenUser & { id: string; type: UserType }>(
    `/system/citizen-login`,
    citizen,
    {
      headers: createServiceRequestHeaders(undefined, systemUser)
    }
  )
  return { ...res.data, type: 'citizen' }
}
