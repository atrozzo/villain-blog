FROM alpine:edge

MAINTAINER angelo.trozzo@gmail.com

RUN \
apk add --no-cache openjdk8 && \
mongodb && \
rm /usr/bin/mongoperf

VOLUME /data/db
EXPOSE 27017 28017

COPY files/UnlimitedJCEPolicyJDK8/* \
  /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/




CMD [ "mongod" ]



