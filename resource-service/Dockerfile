# Use an official Java runtime as a base image
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

# Copy the application JAR file (adjust the name)
COPY target/resource-service-1.0-SNAPSHOT.jar resource-service.jar

# Expose port 8083 for the service
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "resource-service.jar"]