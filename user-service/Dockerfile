FROM eclipse-temurin:21-jre
ARG JAR=build/libs/user-service-*.jar
WORKDIR /app
COPY ${JAR} app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]

