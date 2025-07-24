FROM openjdk:17-jdk-slim
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Run the application
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "target/product-service-1.0.0.jar"]