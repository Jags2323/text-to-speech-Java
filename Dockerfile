FROM openjdk:18-alpine

WORKDIR /app

COPY pom.xml .

RUN mvn clean package -Pprod

COPY target/*.jar .

CMD ["java", "-jar", "*.jar"]

EXPOSE 8080
