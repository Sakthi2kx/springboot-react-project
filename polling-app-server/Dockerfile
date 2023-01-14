#### Stage 1: Build the application
FROM openjdk:11 
ADD target/polls-0.0.1-SNAPSHOT.jar polls-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","polls-0.0.1-SNAPSHOT.jar"]
