# Setup CA
```
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Demo-Kafka" -keyout ca-key -out ca-cert -nodes
```

# CERTS Server 
Result "kafka.server.keystore.jks" and "kafka.server.truststore.jks"
```
keytool -genkey -keystore kafka.server.keystore.jks -validity 365 -storepass password -keypass password -dname "CN=localhost" -storetype pkcs12 -keyalg RSA
```

```
keytool -keystore kafka.server.keystore.jks -certreq -file cert-file -storepass password -keypass password
```

```
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-file-signed -days 365 -CAcreateserial -passin pass:password
```

```
keytool -keystore kafka.server.keystore.jks -alias CARoot -import -file ca-cert -storepass password -keypass password -noprompt
```

```
keytool -keystore kafka.server.keystore.jks -import -file cert-file-signed -storepass password -keypass password -noprompt
```
------------
```
keytool -keystore kafka.server.truststore.jks -alias CARoot -import -file ca-cert -storepass password -keypass password -noprompt
```
# CERTS Client
Result "kafka.client.keystore.jks" and "kafka.client.truststore.jks"
```
keytool -genkey -keystore kafka.client.keystore.jks -validity 365 -storepass password -keypass password -dname "CN=localhost" -storetype pkcs12 -keyalg RSA
```
```
keytool -keystore kafka.client.keystore.jks -certreq -file cert-client-file -storepass password -keypass password
```
```
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-client-file -out cert-client-file-signed -days 365 -CAcreateserial -passin pass:password
```
```
keytool -keystore kafka.client.keystore.jks -alias CARoot -import -file ca-cert -storepass password -keypass password -noprompt
```
```
keytool -keystore kafka.client.keystore.jks -import -file cert-client-file-signed -storepass password -keypass password -noprompt
```
------------
```
keytool -keystore kafka.client.truststore.jks -alias CARoot -import -file ca-cert -storepass password -keypass password -noprompt
```

# CONFIG SERVER
    ADVERTISED_HOST_NAME: 127.0.0.1
    ADVERTISED_LISTENERS: 'SSL://kafka:29092,PLAINTEXT://localhost:9092'
    LISTENERS: 'SSL://:29092,PLAINTEXT://:9092'
    AUTO_CREATE_TOPICS_ENABLE: 'true'
    ZOOKEEPER_CONNECT: 'zookeeper:2181'
    SSL_ENABLED_PROTOCOLS: 'TLSv1.2,TLSv1.3,TLSv1.1'
    SSL_PROTOCOL: 'TLSv1.1'
    SSL_KEYSTORE_LOCATION: '/certs/kafka.server.keystore.jks'
    SSL_KEYSTORE_PASSWORD: 'maciek'
    SSL_CLIENT_AUTH: 'required'
    SSL_KEY_PASSWORD: 'maciek'
    SSL_TRUSTSTORE_LOCATION: '/certs/kafka.server.truststore.jks'
    SSL_TRUSTSTORE_PASSWORD: 'maciek'
    SSL_ENDPOINT_IDENTIFICATION_ALGORITHM: ''
# CONFIG CLIENT

    ssl.endpoint.identification.algorithm: ""
    ssl.keystore.password: keystorePassword
    ssl.keystore.location: keystoreLocation
    ssl.truststore.password: truststorePassword
    ssl.truststore.location: truststoreLocation
    ssl.key.password: password
    ssl.protocol: "TLSv1.2"
    security.protocol: "SSL"

GL&HF