FROM amazoncorretto:17-alpine-jdk
# need replace the .jar file => use mvn clean install
COPY target/AuthenTicket-0.0.1-SNAPSHOT.jar AuthenTicket-0.0.1-SNAPSHOT.jar
#expose the port
EXPOSE 8080
ENTRYPOINT ["java","-jar","/AuthenTicket-0.0.1-SNAPSHOT.jar"]