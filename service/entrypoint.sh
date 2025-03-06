#!/usr/bin/env bash

set -euo pipefail

# For log tagging (with a default value and error logging without crashing)
# shellcheck disable=SC2155
export HOST_IP=$(curl --max-time 10 --silent --fail --show-error http://169.254.169.254/latest/meta-data/local-ipv4 || printf 'UNAVAILABLE')

if [ "${VOLTTI_ENV:-local}" != "local" ]; then
  s3download "$DEPLOYMENT_BUCKET" config /config
fi

if [ "${DD_PROFILING_ENABLED:-false}" = "true" ]; then
  export DD_AGENT_HOST="${DD_AGENT_HOST:-localhost}"
  export DD_TRACE_AGENT_PORT="${DD_TRACE_AGENT_PORT:-8126}"
  export DD_JMXFETCH_STATSD_HOST="${DD_JMXFETCH_STATSD_HOST:-$HOST_IP}"
  export DD_ENV="${DD_ENV:-$VOLTTI_ENV}"
  export DD_VERSION="${DD_VERSION:-$APP_COMMIT}"
  export DD_SERVICE="${DD_SERVICE:-${APP_NAME:-vekkuli-service}}"
  export DD_TRACE_REMOVE_INTEGRATION_SERVICE_NAMES_ENABLED="${DD_TRACE_REMOVE_INTEGRATION_SERVICE_NAMES_ENABLED:-true}"
  export DD_SERVICE_MAPPING="${DD_SERVICE_MAPPING:-postgresql:vekkuli-postgresql}"
  #export DD_TRACE_OTEL_ENABLED=true

  if [ "$DD_AGENT_HOST" = "UNAVAILABLE" ]; then
    echo "Invalid DD_AGENT_HOST. Is it unset and not in AWS environment?"
    exit 1
  fi

  # shellcheck disable=SC2086
  exec java \
    -Ddd.jmxfetch.config=/etc/jmxfetch/conf.yaml \
    -Ddd.profiling.enabled=true \
    -Ddd.logs.injection=true \
    -Ddd.trace.sample.rate=1 \
    -javaagent:/opt/dd-java-agent.jar \
    -XX:FlightRecorderOptions=stackdepth=256 \
    -cp . -server $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher "$@"
else
  # shellcheck disable=SC2086
  exec java -cp . -server $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher "$@"
fi
