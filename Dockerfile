FROM maven:latest

RUN apt-get update && apt-get install -y sqlite3 make

RUN make build-no-tests

WORKDIR /app

COPY target/weshare-mvc-exercise-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar /app/weshare-webapi.jar

EXPOSE 5050

ENTRYPOINT ["java", "-jar", "weshare-webapi.jar"]

