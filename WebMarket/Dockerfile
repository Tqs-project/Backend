FROM adoptopenjdk/openjdk11:jdk-11.0.1.13-alpine

COPY . /src

WORKDIR /src
RUN ./mvnw package -DskipTests

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "target/WebMarket-0.0.1-SNAPSHOT.jar" ]