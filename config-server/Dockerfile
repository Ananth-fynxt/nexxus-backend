FROM eclipse-temurin:21-jre
ARG JAR=build/libs/config-server-*.jar
WORKDIR /app
COPY ${JAR} app.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app/app.jar"]

