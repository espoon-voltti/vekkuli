# syntax=docker/dockerfile:1.27.0
# SPDX-FileCopyrightText: 2024-2025 City of Espoo
#
# SPDX-License-Identifier: LGPL-2.1-or-later

ARG NGINX_VERSION=1.31.3

FROM node:24.20.0-trixie-slim AS builder

ARG CACHE_BUST=none

WORKDIR /project

#COPY ./.yarn ./.yarn
COPY ./package.json ./yarn.lock ./.yarnrc.yml ./
RUN corepack enable && yarn install --immutable

COPY . .

RUN yarn build

FROM builder AS test

RUN yarn lint 
RUN yarn type-check 

# The -otel image variant ships the official ngx_otel_module.so, so nginx tracing
# no longer depends on a per-version prebuilt module (the deprecated
# opentracing-contrib / dd-opentracing-cpp downloads that pinned us to nginx
# 1.29.8). Traces are exported over OTLP to the Datadog agent; see nginx.conf.template.
FROM nginx:${NGINX_VERSION}-otel

ARG CACHE_BUST=none

ENV NGINX_ENV=local \
    TZ=UTC

# https://github.com/hairyhenderson/gomplate
ARG GOMPLATE_VERSION=v4.0.1
ARG GOMPLATE_SHA256="5aeae23cbc51e243967162a62edba0098218668a09cdaa6d6de1b228c070990f"

RUN echo "CACHE_BUST=${CACHE_BUST}" \
    && apt-get update \
    && apt-get -y dist-upgrade \
    && apt-get remove --auto-remove -y nginx-module-image-filter nginx-module-njs nginx-module-xslt \
    && curl -sSfL "https://github.com/hairyhenderson/gomplate/releases/download/${GOMPLATE_VERSION}/gomplate_linux-amd64" \
    -o /bin/gomplate \
    && chmod +x /bin/gomplate \
    && echo "${GOMPLATE_SHA256}  /bin/gomplate" | sha256sum -c - \
    && apt-get remove --auto-remove -y curl \
    && rm -rf /var/lib/apt/lists/*

COPY ./nginx/bin/ /bin/
COPY ./nginx/etc/ /etc/

RUN rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf \
 && mkdir -p /nginx \
 && chown nginx:nginx /nginx

VOLUME /nginx

ENTRYPOINT ["/bin/proxy-entrypoint.sh"]
CMD ["nginx", "-c", "/nginx/config/nginx.conf", "-g", "daemon off;"]

COPY --from=builder /project/dist/esbuild/citizen-frontend/ /static/

ARG build=none
ARG commit=none

ENV APP_BUILD="$build" \
    APP_COMMIT="$commit"
LABEL fi.espoo.build="$build" \
    fi.espoo.commit="$commit"
