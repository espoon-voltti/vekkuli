FROM node:24.14.1-bookworm-slim AS base

ARG CACHE_BUST=none

WORKDIR /app

# Upgrade to the latest Yarn version
RUN corepack enable && corepack prepare yarn@stable --activate

FROM base AS builder

COPY ./.yarn ./.yarn
COPY ./package.json ./yarn.lock ./.yarnrc.yml ./

RUN yarn install --immutable

COPY . .

RUN yarn build

FROM builder AS test

RUN yarn lint
#RUN yarn test

FROM builder

ENV NODE_ENV="production"

ARG S3_DOWNLOADER_VERSION=v1.5.1
ARG S3_DOWNLOADER_SHA256="cb751589ca60749c42b65009671d6076435e7454b8980ee98884e296a1ec6cb8"

RUN apt-get update \
 && apt-get -y dist-upgrade \
 && apt-get -y install curl \
 && curl -sSfL https://github.com/espoon-voltti/s3-downloader/releases/download/${S3_DOWNLOADER_VERSION}/s3downloader-linux-amd64 \
       -o /bin/s3download \
 && echo "${S3_DOWNLOADER_SHA256}  /bin/s3download" | sha256sum -c - \
 && chmod +x /bin/s3download

ARG build=none
ARG commit=none

ENV APP_BUILD="$build" \
    APP_COMMIT="$commit"

LABEL fi.espoo.build="$build" \
      fi.espoo.commit="$commit"

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["node", "dist/index.js"]
