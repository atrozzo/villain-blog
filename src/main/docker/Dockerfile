FROM alpine:edge

MAINTAINER angelo.trozzo@gmail.com

RUN \
apk add --no-cache openjdk8 \
mongodb && \
rm /usr/bin/mongoperf

VOLUME /data/db
EXPOSE 27017 28017

COPY files/UnlimitedJCEPolicyJDK8/* \
  /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/

 CMD [ "mongod" ]

 VOLUME /tmp
 ADD supervillian-0.0.1-SNAPSHOT.jar app.jar
 RUN sh -c 'touch /app.jar'
 ENV JAVA_OPTS=""
 ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]






