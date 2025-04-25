# FROM eclipse-temurin:17-jre as builder
FROM eclipse-temurin:11-jdk-alpine-3.21 as builder
WORKDIR extracted
ADD output/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:11-jdk-alpine-3.21
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

EXPOSE 8087

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]