FROM openjdk:17-jdk-slim
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Run the application with proper JVM settings
EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/product-service-1.0.0.jar"]