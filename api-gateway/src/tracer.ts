// SPDX-FileCopyrightText: 2017-2025 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import tracer from 'dd-trace'

import {
  volttiEnv,
  appName,
  appCommit,
  tracingEnabled,
  traceAgentHostname,
  traceAgentPort,
  profilingEnabled
} from './config.js'

if (tracingEnabled) {
  tracer.tracer.init({
    env: volttiEnv,
    service: appName,
    version: appCommit,
    hostname: traceAgentHostname,
    port: traceAgentPort,
    profiling: profilingEnabled,
    logInjection: true,
    runtimeMetrics: true
  }) // initialized in a different file to avoid hoisting.
}

export default tracer
