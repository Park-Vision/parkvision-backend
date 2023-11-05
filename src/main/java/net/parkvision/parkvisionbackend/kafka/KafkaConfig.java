package net.parkvision.parkvisionbackend.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

//@Configuration
public class KafkaConfig {
    @Autowired
    KafkaProperties kafkaProperties;


    @Bean
    public KafkaAdmin kafkaAdmin() {

        KafkaAdmin kafkaAdmin = new KafkaAdmin(kafkaProperties.buildAdminProperties());
        kafkaAdmin.setFatalIfBrokerNotAvailable(kafkaProperties.getAdmin().isFailFast());
        return kafkaAdmin;
    }
}
