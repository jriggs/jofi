# ---------- Stage 1: Build the Spring Boot app ----------
FROM eclipse-temurin:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Ensure the Maven wrapper is executable
RUN chmod +x mvnw

# Build the application without running tests
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Run the built app ----------
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
