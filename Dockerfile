# Build stage
FROM gradle:jdk21-alpine AS build

ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

RUN gradle build || return 0

COPY . .

RUN gradle clean build


# Example of custom Java runtime using jlink in a multi-stage container build
FROM eclipse-temurin:21-jdk-alpine
RUN mkdir /opt/app
COPY --from=build /app/build/libs/*.jar  /opt/app/japp.jar
CMD ["java", "-jar", "/opt/app/japp.jar"]

