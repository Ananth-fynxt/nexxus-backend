FROM eclipse-temurin:21-jre
ARG JAR=build/libs/api-gateway-*.jar
WORKDIR /app
COPY ${JAR} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

