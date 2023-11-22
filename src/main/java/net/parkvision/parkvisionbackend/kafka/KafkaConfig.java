package net.parkvision.parkvisionbackend.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Autowired
    KafkaProperties kafkaProperties;
    @Value(value = "${spring.kafka.ssl.key-store-password}")
    private String keystorePassword;
    @Value(value = "${spring.kafka.ssl.key-store-location}")
    private String keystoreLocation;
    @Value(value = "${spring.kafka.ssl.trust-store-password}")
    private String truststorePassword;
    @Value(value = "${spring.kafka.ssl.trust-store-location}")
    private String truststoreLocation;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildAdminProperties());
        props.put("ssl.endpoint.identification.algorithm", "");
        props.put("ssl.keystore.password", keystorePassword);
        props.put("ssl.protocol", "TLSv1.2");
        props.put("ssl.keystore.location", keystoreLocation);
        props.put("ssl.truststore.password", truststorePassword);
        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.key.password", "maciek");
        props.put("security.protocol", "SSL");

        KafkaAdmin kafkaAdmin = new KafkaAdmin(props);
        kafkaAdmin.setFatalIfBrokerNotAvailable(kafkaProperties.getAdmin().isFailFast());
        return kafkaAdmin;
    }
}
