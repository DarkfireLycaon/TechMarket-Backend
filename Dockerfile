# Paso 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Paso 2: Ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el jar desde el paso anterior.
# Aseguramos que tomamos el archivo correcto
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]