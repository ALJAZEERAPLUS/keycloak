FROM maven:3.6.1-alpine

ENV ALPINE_MIRROR "http://dl-cdn.alpinelinux.org/alpine"
RUN echo "${ALPINE_MIRROR}/edge/main" >> /etc/apk/repositories
RUN apk add --no-cache nodejs-current --repository http://dl-cdn.alpinelinux.org/alpine/v3.6/main/