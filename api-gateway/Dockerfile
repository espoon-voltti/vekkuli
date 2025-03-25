FROM node:22.14.0-bookworm-slim AS base

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

ENV NODE_ENV production

RUN apt-get update \
 && apt-get -y dist-upgrade \
 && apt-get -y install curl \
 && curl -sSfL https://github.com/espoon-voltti/s3-downloader/releases/download/v1.4.1/s3downloader-linux-amd64 \
       -o /bin/s3download \
 && chmod +x /bin/s3download

ARG build=none
ARG commit=none

ENV APP_BUILD "$build"
ENV APP_COMMIT "$commit"

LABEL fi.espoo.build="$build" \
      fi.espoo.commit="$commit"

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["node", "dist/index.js"]
