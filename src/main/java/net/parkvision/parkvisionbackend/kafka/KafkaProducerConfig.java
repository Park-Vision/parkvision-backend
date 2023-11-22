package net.parkvision.parkvisionbackend.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.ssl.key-store-password}")
    private String keystorePassword;
    @Value(value = "${spring.kafka.ssl.key-store-location}")
    private String keystoreLocation;
    @Value(value = "${spring.kafka.ssl.trust-store-password}")
    private String truststorePassword;
    @Value(value = "${spring.kafka.ssl.trust-store-location}")
    private String truststoreLocation;
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put("ssl.endpoint.identification.algorithm", "");
        configProps.put("ssl.keystore.password", keystorePassword);
        configProps.put("ssl.keystore.location", keystoreLocation);
        configProps.put("ssl.truststore.password", truststorePassword);
        configProps.put("ssl.truststore.location", truststoreLocation);
        configProps.put("ssl.key.password", "maciek");
        configProps.put("ssl.protocol", "TLSv1.2");
        configProps.put("security.protocol", "SSL");
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}