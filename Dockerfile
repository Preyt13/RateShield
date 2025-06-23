# Use official OpenJDK 17 image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Make Maven Wrapper executable (in case)
RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot's default port
EXPOSE 8080

# Run the JAR (update name if needed)
CMD ["java", "-jar", "target/RateShield-0.0.1-SNAPSHOT.jar"]
