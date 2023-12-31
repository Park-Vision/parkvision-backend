package net.parkvision.parkvisionbackend.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.ssl.key-store-password}")
    private String keystorePassword;
    @Value(value = "${spring.kafka.ssl.key-store-location}")
    private String keystoreLocation;
    @Value(value = "${spring.kafka.ssl.trust-store-password}")
    private String truststorePassword;
    @Value(value = "${spring.kafka.ssl.trust-store-location}")
    private String truststoreLocation;


    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("ssl.endpoint.identification.algorithm", "");
        props.put("ssl.keystore.password", keystorePassword);
        props.put("ssl.keystore.location", keystoreLocation);
        props.put("ssl.truststore.password", truststorePassword);
        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.key.password", keystorePassword);
        props.put("ssl.protocol", "TLSv1.2");
        props.put("security.protocol", "SSL");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}