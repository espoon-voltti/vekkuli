# syntax=docker/dockerfile:1.6.0
# SPDX-FileCopyrightText: 2024-2025 City of Espoo
#
# SPDX-License-Identifier: LGPL-2.1-or-later

ARG NGINX_VERSION=1.26.0

FROM node:22.12.0-bookworm-slim AS builder

ARG CACHE_BUST=2025-W05

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

ARG CACHE_BUST=2025-W05

ENV NGINX_ENV=local \
  TZ=UTC

ARG S3_DOWNLOADER_VERSION=v1.4.1
ARG S3_DOWNLOADER_SHA256="520ea232e83a7cefe2a87d4f2af8433e383a4351464e213b7dd3b78ca0dc200f"

RUN apt-get update \
  && apt-get -y dist-upgrade \
  && apt-get -y --no-install-recommends install ruby \
  && apt-get remove --auto-remove -y nginx-module-image-filter nginx-module-njs nginx-module-xslt \
  && curl -sSfL https://github.com/espoon-voltti/s3-downloader/releases/download/${S3_DOWNLOADER_VERSION}/s3downloader-linux-amd64 \
  -o /bin/s3download \
  && chmod +x /bin/s3download \
  && echo "${S3_DOWNLOADER_SHA256}  /bin/s3download" | sha256sum -c - \
  && apt-get remove --auto-remove -y curl \
  && rm -rf /var/lib/apt/lists/*

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
