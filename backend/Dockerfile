# ---------- build ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the necessary files for Maven to work
COPY pom.xml .

# Download dependencies (this will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ---------- run ----------
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the built application
COPY --from=build /workspace/target/*-runner.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables
ENV QUARKUS_HTTP_PORT=8080
ENV QUARKUS_PROFILE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
