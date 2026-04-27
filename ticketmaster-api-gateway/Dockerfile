# 1. Build mərhələsi (alpine yerinə jammy yazdıq)
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
# Gradle-in çökməməsi üçün --no-daemon əlavə etdik
RUN ./gradlew bootJar -x test --no-daemon

# 2. Run mərhələsi (yenə jammy)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]