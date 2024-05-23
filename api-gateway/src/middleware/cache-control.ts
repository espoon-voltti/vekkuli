// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import express from 'express'
import nocache from 'nocache'

export const cacheControl = (
  allowCaching: (req: express.Request) => 'allow-cache' | 'forbid-cache'
): express.RequestHandler => {
  const forbidCaching = nocache()
  return (req, res, next) => {
    return allowCaching(req) === 'allow-cache'
      ? next()
      : forbidCaching(req, res, next)
  }
}
