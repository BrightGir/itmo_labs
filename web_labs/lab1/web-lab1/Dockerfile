FROM gradle:8.1.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar --no-daemon


FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

# ВАЖНО: Используем наш порт 9000
ENTRYPOINT ["java", "-DFCGI_PORT=9000", "-jar", "app.jar"]