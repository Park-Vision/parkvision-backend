FROM openjdk:17-alpine AS TEMP_BUILD_IMAGE
COPY build.gradle settings.gradle gradlew /
COPY gradle /gradle
RUN ./gradlew build || return 0
COPY . .
RUN ./gradlew bootJar

# Production
FROM openjdk:17-alpine
COPY ssl2/kafka.client.truststore.jks kafka.client.truststore.jks
COPY ssl2/kafka.client.keystore.jks kafka.client.keystore.jks
COPY --from=TEMP_BUILD_IMAGE /build/libs/parkvision-backend-0.0.1-SNAPSHOT.jar .
CMD ["java","-jar","parkvision-backend-0.0.1-SNAPSHOT.jar"]
