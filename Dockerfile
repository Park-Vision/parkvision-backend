FROM openjdk:17-alpine
COPY ssl2/kafka.client.truststore.jks kafka.client.truststore.jks
COPY ssl2/kafka.client.keystore.jks kafka.client.keystore.jks
ARG JAR_FILE=build/libs/\*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]