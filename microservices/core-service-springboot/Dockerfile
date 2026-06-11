# Build stage using Maven and JDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Execution stage using Eclipse Temurin JRE 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/ticklora-core-1.0.0.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
