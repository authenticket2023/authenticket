# Use the Amazon Corretto 17 image as the base image
FROM amazoncorretto:17-alpine-jdk

# Copy the JAR file from your project's target directory to the container
COPY target/AuthenTicket-0.0.1-SNAPSHOT.jar /AuthenTicket-0.0.1-SNAPSHOT.jar

# Set the timezone to Asia/Singapore
ENV TZ="Asia/Singapore"

# Copy the icon.png file from your local file system to the container
COPY src/main/resources/static/img/icon.png src/main/resources/static/img/icon.png

# Expose port 8080 for your application
EXPOSE 8080

# Specify the entry point to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "/AuthenTicket-0.0.1-SNAPSHOT.jar"]
