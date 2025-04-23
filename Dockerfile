FROM openjdk:21-jdk-slim

WORKDIR /app

COPY /target/licenseapi.jar licenseapi.jar

EXPOSE 8282

ENTRYPOINT ["java", "-jar", "licenseapi.jar"]

