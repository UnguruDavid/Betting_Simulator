# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled Java application JAR to the container
COPY target/java-maven-example-1.0-SNAPSHOT.jar /app/myapp.jar

# Make port 8081 available to the world outside this container
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
