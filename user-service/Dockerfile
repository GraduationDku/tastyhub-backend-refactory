From eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_NAME=app.jar
COPY --from=builder /app/build/libs/*.jar /app/${JAR_NAME}
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
