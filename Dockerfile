# FROM maven:3.6.1-alpine

# ENV ALPINE_MIRROR "http://dl-cdn.alpinelinux.org/alpine"
# RUN echo "${ALPINE_MIRROR}/edge/main" >> /etc/apk/repositories
# RUN apk add --no-cache nodejs-lastest --repository http://dl-cdn.alpinelinux.org/alpine/v3.6/main/
# RUN apk add --update nodejs
# RUN apk add --update npm
# RUN apk add --update nodejs-npm

FROM ubuntu:latest

MAINTAINER Kai Winter (https://github.com/kaiwinter)

# this is a non-interactive automated build - avoid some warning messages
ENV DEBIAN_FRONTEND noninteractive

# update dpkg repositories
RUN apt-get update 

# install wget
RUN apt-get install -y wget

# get maven 3.3.9
RUN wget --no-verbose -O /tmp/apache-maven-3.3.9.tar.gz http://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz

# verify checksum
RUN echo "516923b3955b6035ba6b0a5b031fbd8b /tmp/apache-maven-3.3.9.tar.gz" | md5sum -c

# install maven
RUN tar xzf /tmp/apache-maven-3.3.9.tar.gz -C /opt/
RUN ln -s /opt/apache-maven-3.3.9 /opt/maven
RUN ln -s /opt/maven/bin/mvn /usr/local/bin
RUN rm -f /tmp/apache-maven-3.3.9.tar.gz
ENV MAVEN_HOME /opt/maven

# remove download archive files
RUN apt-get clean

# set shell variables for java installation
RUN apt-get update
RUN apt-get install -y openjdk-8-jdk
RUN update-alternatives --config java
RUN update-alternatives --config javac

# install npm
RUN curl -sL https://deb.nodesource.com/setup_lts.x | bash -
RUN apt-get install -y nodejs
RUN apt-get install -y npm
RUN chown -R 1000:1000 /.npm