# Use an official OpenJDK 21 image as a base
FROM openjdk:21-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the gradle wrapper files and build scripts
COPY gradle /app/gradle
COPY gradlew /app/
COPY build.gradle.kts /app/

# Copy the source code to the container
COPY src /app/src

# Make gradlew executable (if it's not already executable)
RUN chmod +x gradlew

# Install dependencies and build the project with Gradle using the wrapper
RUN ./gradlew build

# Expose the application port (optional, if it's a web app)
EXPOSE 8080

# Set the entry point to run the app
CMD ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]
