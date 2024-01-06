FROM gradle:8.4-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM sapmachine:17-jre-ubuntu

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/

ENTRYPOINT ["java", "-jar", "/app/movie-manager-0.0.1-SNAPSHOT.jar"]