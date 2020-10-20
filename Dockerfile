FROM maven:3-alpine

RUN apt update \
&& apt install node -y \
&& apt install npm -y