# Use Maven image to build the application
FROM maven:latest AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml ./
COPY src ./src

# Run Maven package command
RUN mvn package -DskipTests -X

# Use a smaller base image for the final application
FROM openjdk:17-jdk-slim

# Set the working directory for the final image
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/weshare-mvc-exercise-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar ./weshare-webapi.jar

# Expose the application port
EXPOSE 5050

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "weshare-webapi.jar"]
