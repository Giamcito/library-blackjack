FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom y fuentes
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el jar generado por la fase de build
COPY --from=build /app/target/algoritmos-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
