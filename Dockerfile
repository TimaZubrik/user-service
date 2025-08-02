FROM openjdk:24-jdk-slim-bullseye AS builder

RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM openjdk:24-jdk-slim-bullseye

WORKDIR /app

COPY --from=builder /app/target/user-service-0.0.1-SNAPSHOT.jar user-service.jar

ENTRYPOINT ["java","-jar","user-service.jar"]
