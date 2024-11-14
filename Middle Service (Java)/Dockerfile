# build the jar file with maven
FROM maven:3.9.3-eclipse-temurin-11
RUN mkdir -p /JarLocation
WORKDIR /JarLocation
COPY MiddleService/pom.xml /JarLocation
COPY MiddleService/src /JarLocation/src
RUN mvn -f pom.xml clean package

# use the jar file to compile project
FROM eclipse-temurin:11-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=/JarLocation/java-0.0.3.jar
ADD . java-0.0.3.jar
ENTRYPOINT ["java","-jar","/java-0.0.3.jar"]
