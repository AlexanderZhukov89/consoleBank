FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Копируем Maven проект
COPY pom.xml .
COPY src ./src

# Сборка приложения
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]