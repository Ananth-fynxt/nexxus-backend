FROM eclipse-temurin:21-jre
ARG JAR=build/libs/eureka-server-*.jar
WORKDIR /app
COPY ${JAR} app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]

