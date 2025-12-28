FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :apiGateway-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_NAME=app.jar
COPY --from=builder /app/apiGateway-service/build/libs/*.jar /app/${JAR_NAME}
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app/app.jar"]
