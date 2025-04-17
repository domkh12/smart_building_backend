# PHASE 1 - Download & Install JDK

FROM ghcr.io/graalvm/jdk-community:21
WORKDIR app
ADD ./build/libs/smart-building-api-1.0.jar /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=stage", "/app/smart-building-api-1.0.jar"]