# Use an official Maven image as a build environment
FROM maven:3.8-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Copy only the pom.xml file to cache dependencies
COPY ./pom.xml /app/

# Create a volume for Maven dependencies
VOLUME /root/.m2

# Download dependencies. This layer will be cached.
RUN mvn dependency:go-offline

# Copy the source code
COPY . /app

# Build the application
RUN mvn clean install

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/clone-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the application when the container launches
CMD ["java", "-jar", "app.jar"]
