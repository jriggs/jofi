# ---------- Stage 1: Build the Spring Boot app ----------
FROM eclipse-temurin:21-jdk AS build

# Set working directory inside the container
WORKDIR /app

# Copy everything from the /api folder into the container
COPY . .

# Ensure the Maven wrapper is executable
RUN chmod +x mvnw

# Build the application without running tests
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Run the built app ----------
FROM eclipse-temurin:21-jre

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
