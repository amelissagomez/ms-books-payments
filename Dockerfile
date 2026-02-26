FROM maven:3.9.12-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre
EXPOSE 8082
COPY --from=build /target/ms-books-payments-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]