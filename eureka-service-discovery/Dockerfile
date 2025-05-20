# Dockerfile

# --- Build Stage ---
FROM maven:3.9.9-eclipse-temurin-17-alpine as build

# Set the working directory for the build stage
WORKDIR /app

# Copy only the pom.xml file to leverage Docker's caching mechanism for dependencies
COPY pom.xml .

# Download dependencies only (caches dependencies if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source files into the container
COPY src ./src

# Build the project and skip tests for a faster build
RUN mvn clean package -Dmaven.test.skip=true

# --- Runtime Stage ---
FROM maven:3.9.9-eclipse-temurin-17-alpine as runtime

# Set the working directory for the runtime stage
WORKDIR /app

# Copy the built JAR file from the build stage to the runtime stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8761

# Define the command to run the application
CMD ["java", "-jar", "app.jar"]