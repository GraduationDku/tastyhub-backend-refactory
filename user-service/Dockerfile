FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :user-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_NAME=app.jar
COPY --from=builder /app/user-service/build/libs/*.jar ${JAR_NAME}
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
