FROM eclipse-temurin:21-jre

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests package

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/worldcupmatchtracker-0.0.1-SNAPSHOT.jar"]
