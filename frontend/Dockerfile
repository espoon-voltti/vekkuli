# syntax=docker/dockerfile:1.17.1
# SPDX-FileCopyrightText: 2024-2025 City of Espoo
#
# SPDX-License-Identifier: LGPL-2.1-or-later

ARG NGINX_VERSION=1.26.0

FROM node:22.16.0-bookworm-slim AS builder

ARG CACHE_BUST=none

WORKDIR /project

#COPY ./.yarn ./.yarn
COPY ./package.json ./yarn.lock ./.yarnrc.yml ./
RUN corepack enable && yarn install --immutable

COPY . .

RUN yarn build

FROM builder AS test

# RUN yarn lint # disabled for now
# RUN yarn type-check # disabled for now

FROM nginx:${NGINX_VERSION}

ARG CACHE_BUST=none

ENV NGINX_ENV=local \
    TZ=UTC

# https://github.com/hairyhenderson/gomplate
ARG GOMPLATE_VERSION=v4.0.1
ARG GOMPLATE_SHA256="5aeae23cbc51e243967162a62edba0098218668a09cdaa6d6de1b228c070990f"

RUN apt-get update \
 && apt-get -y dist-upgrade \
 && apt-get remove --auto-remove -y nginx-module-image-filter nginx-module-njs nginx-module-xslt \
 && curl -sSfL "https://github.com/hairyhenderson/gomplate/releases/download/${GOMPLATE_VERSION}/gomplate_linux-amd64" \
       -o /bin/gomplate \
 && chmod +x /bin/gomplate \
 && echo "${GOMPLATE_SHA256}  /bin/gomplate" | sha256sum -c - \
 && rm -rf /var/lib/apt/lists/*

# https://docs.datadoghq.com/tracing/trace_collection/proxy_setup/nginx # update version using get_latest_release
ARG OPENTRACING_NGINX_VERSION=v0.35.1
ARG OPENTRACING_NGINX_SHA256="34e0fa9f110a6052a4f75451bc93e4de8d1a32229b523fb4748feee6bfb1d3ba"
ARG DD_OPENTRACING_CPP_VERSION=v1.3.7
ARG DD_OPENTRACING_CPP_SHA256="df9a8097c3d6f6e5a96f96db0f3919b5b7b35df3d10c5fcc530707a878e2dd00"

RUN cd /tmp \
 && curl -sSfLO "https://github.com/opentracing-contrib/nginx-opentracing/releases/download/${OPENTRACING_NGINX_VERSION}/linux-amd64-nginx-${NGINX_VERSION}-ngx_http_module.so.tgz" \
 && echo "${OPENTRACING_NGINX_SHA256}  linux-amd64-nginx-${NGINX_VERSION}-ngx_http_module.so.tgz" | sha256sum -c - \
 && tar zxf "linux-amd64-nginx-${NGINX_VERSION}-ngx_http_module.so.tgz" -C /usr/lib/nginx/modules \
 && rm "linux-amd64-nginx-${NGINX_VERSION}-ngx_http_module.so.tgz" \
 && curl -sSfLO https://github.com/DataDog/dd-opentracing-cpp/releases/download/${DD_OPENTRACING_CPP_VERSION}/linux-amd64-libdd_opentracing_plugin.so.gz \
 && echo "${DD_OPENTRACING_CPP_SHA256}  linux-amd64-libdd_opentracing_plugin.so.gz" | sha256sum -c - \
 && gunzip linux-amd64-libdd_opentracing_plugin.so.gz -c > /usr/local/lib/libdd_opentracing_plugin.so \
 && rm linux-amd64-libdd_opentracing_plugin.so.gz \
 && apt-get remove --auto-remove -y curl

COPY ./nginx/bin/ /bin/
COPY ./nginx/etc/ /etc/

ENTRYPOINT ["/bin/proxy-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]

COPY --from=builder /project/dist/esbuild/citizen-frontend/ /static/

ARG build=none
ARG commit=none

ENV APP_BUILD="$build" \
    APP_COMMIT="$commit"
LABEL fi.espoo.build="$build" \
      fi.espoo.commit="$commit"
