FROM alpine:3.20.1

RUN apk update
RUN apk --no-cache add openjdk21

ARG gradle_version=8.7
ADD https://services.gradle.org/distributions/gradle-${gradle_version}-bin.zip /root/gradle.zip
RUN unzip -n /root/gradle.zip -d /root/gradle/
RUN rm /root/gradle.zip
ENV PATH "$PATH:/root/gradle/bin/"

VOLUME /root/m2-repo/
VOLUME /root/modules/

