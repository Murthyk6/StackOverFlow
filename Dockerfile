# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/clone-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Define environment variables if needed
# ENV VARIABLE_NAME=value

# Run the application when the container launches
CMD ["java", "-jar", "app.jar"]
