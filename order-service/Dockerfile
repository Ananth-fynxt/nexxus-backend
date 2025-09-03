FROM eclipse-temurin:21-jre
ARG JAR=build/libs/order-service-*.jar
WORKDIR /app
COPY ${JAR} app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]

