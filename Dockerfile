# USAGE
# docker build -t booking .
# docker run --rm -it -p 3000:3000 booking
FROM maven:3.5-jdk-8 AS builder
FROM maven:3.6.3-openjdk-15-slim AS builder
WORKDIR /workspace
COPY . .
RUN mvn clean package -DskipTests

FROM adoptopenjdk/openjdk15:x86_64-alpine-jre-15.0.2_7
WORKDIR /workspace
COPY --from=builder /workspace/target/*.jar ./booking.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "booking.jar"]
