FROM eclipse-temurin:21.0.1_12-jre-ubi9-minimal AS builder
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar

ARG PROFILE
ENV PROFILE=${PROFILE}

RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21.0.1_12-jre-ubi9-minimal

RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
USER javauser

COPY --from=builder dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder internal-dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "org.springframework.boot.loader.JarLauncher"]
