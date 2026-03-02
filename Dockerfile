FROM openjdk:17-jdk-slim AS build
WORKDIR app
COPY target/*.jar .
RUN chmod +x *.jar

FROM openjdk:17-jre-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY --from=build /app/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]