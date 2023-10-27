FROM gradle:7-jdk-jammy as module-build

WORKDIR /module/app

COPY . .

RUN gradle clean build -x test

FROM openjdk:17.0.2-jdk-slim as production

USER root

RUN apt-get update -y && apt-get install -y jq curl

USER 1000

WORKDIR /app

COPY --from=module-build --chown=1000:1000 /module/app/build/libs/book-music-service-0.0.1-SNAPSHOT.jar ./book-music-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "book-music-service-0.0.1-SNAPSHOT.jar"]

FROM production as development

USER root

COPY --from=module-build --chown=1000:1000 /root/.gradle /opt/jboss/.gradle
COPY --from=module-build --chown=1000:1000 /module/app /development


RUN ln -s /opt/gradle/bin/gradle /usr/bin/gradle

USER 1000:1000
