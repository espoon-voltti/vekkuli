// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import express from 'express'
import axios from 'axios'
import { createAuthHeader, AppSessionUser } from '../auth/index.js'
import { serviceUrl } from '../config.js'

export const client = axios.create({
  baseURL: serviceUrl
})

const systemUser: AppSessionUser = {
  id: '00000000-0000-0000-0000-000000000000'
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
  nationalId: string
  name: string
  email?: string
}

export async function userLogin(
  adUser: AdUser
): Promise<AdUser & { id: string }> {
  const res = await client.post<AdUser & { id: string }>(
    `/system/user-login`,
    adUser,
    {
      headers: createServiceRequestHeaders(undefined, systemUser)
    }
  )
  return res.data
}

export async function citizenLogin(
  citizen: CitizenUser
): Promise<CitizenUser & { id: string }> {
  const res = await client.post<CitizenUser & { id: string }>(
    `/system/citizen-login`,
    citizen,
    {
      headers: createServiceRequestHeaders(undefined, systemUser)
    }
  )
  return res.data
}
//
// export async function getUserDetails(
//   req: express.Request,
//   userId: string
// ): Promise<AppUser | undefined> {
//   const { data } = await client.get<AppUser | undefined>(
//     `/system/users/${userId}`,
//     {
//       headers: createServiceRequestHeaders(req, systemUser)
//     }
//   )
//   return data
// }
