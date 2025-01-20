// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import axios, { AxiosError } from 'axios'

export const API_URL = '/api/citizen'

export const client = axios.create({
  baseURL: API_URL,
  paramsSerializer: (params) => {
    const searchParams = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((item) => searchParams.append(key, item))
      } else {
        searchParams.append(key, value as string)
      }
    })
    return searchParams.toString()
  }
})
client.defaults.headers.common['x-vekkuli-csrf'] = '1'

client.interceptors.response.use(undefined, (err: AxiosError) => {
  if (err.response && err.response.status == 401) {
    window.location.replace('/')
  }

  return Promise.reject(err)
})
