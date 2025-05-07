# Use an official Java runtime as a base image
FROM openjdk:17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file (adjust the name if necessary)
COPY target/song-service-1.0-SNAPSHOT.jar song-service.jar

# Expose port 8085 for the service
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "song-service.jar"]