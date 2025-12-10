FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_NAME=app.jar
COPY --from=builder /app/build/libs/*.jar /app/${JAR_NAME}
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
