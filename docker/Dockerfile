FROM eclipse-temurin:21-jre-alpine AS builder
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar

ARG PROFILE
ENV PROFILE=${PROFILE}

RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jre-alpine

# CVE-2023-5363 workaround, until an updated Alpine image has been made available
RUN apk upgrade --update-cache --available && \
    apk add openssl && \
    rm -rf /var/cache/apk/*

RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
USER javauser

COPY --from=builder dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder internal-dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "org.springframework.boot.loader.JarLauncher"]
