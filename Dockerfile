# Use an official OpenJDK 21 image as a base
FROM openjdk:21-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the gradle wrapper files and build scripts
COPY gradle /app/gradle
COPY gradlew /app/
COPY build.gradle.kts /app/

# Copy source code
COPY src /app/src

# Make gradlew executable (if not already executable)
RUN chmod +x gradlew

# Build the project with Gradle using the wrapper
RUN ./gradlew build

# Copy the JAR file into the container
COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Expose the application port (if it's a web app)
EXPOSE 8080

# Set the entry point to run the app
CMD ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]
