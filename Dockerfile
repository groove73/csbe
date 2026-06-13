# Build stage
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew quarkusBuild --no-daemon

# Package stage
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
# Quarkus build generates app jar inside build/quarkus-app/ directory
COPY --from=build /app/build/quarkus-app/ /app/
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAMPercentage=75.0", "-jar", "quarkus-run.jar"]

