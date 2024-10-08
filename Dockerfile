FROM maven:latest AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn package -DskipTests -X

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/weshare-mvc-exercise-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar ./weshare-webapi.jar

EXPOSE 5050

ENTRYPOINT ["java", "-jar", "weshare-webapi.jar"]
