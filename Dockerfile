# Build stage - eclipse-temurin:25-jdk is the build JVM
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
# Remove local gradle.properties (contains Mac-specific java.home path)
RUN rm -f gradle.properties
RUN ./gradlew quarkusBuild --no-daemon -Dquarkus.package.type=uber-jar

# Package stage
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
# Quarkus build generates app jar inside build/quarkus-app/ directory
COPY --from=build /app/build/quarkus-app/ /app/
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAMPercentage=75.0", "-jar", "quarkus-run.jar"]
