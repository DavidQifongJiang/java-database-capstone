FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY app/pom.xml ./pom.xml
COPY app/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp verify

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd --system clinic && useradd --system --gid clinic clinic
COPY --from=build /build/target/back-end-0.0.1-SNAPSHOT.jar /app/clinic.jar
USER clinic
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/clinic.jar"]
