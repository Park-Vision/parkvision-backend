package net.parkvision.parkvisionbackend.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//@Configuration
public class KafkaTopicConfig {

    @Autowired
    KafkaAdmin kafkaAdmin;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    public void createNewTopic(String topicName) throws ExecutionException, InterruptedException {

        Map<String, String> topicConfig = new HashMap<>();
        topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(24 * 60 * 60 * 1000)); // 24 hours retention

        NewTopic newTopic = new NewTopic(topicName, 1, (short) 1).configs(topicConfig);

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        }
    }

    @Bean
    public NewTopic topicInfo() {
        return new NewTopic("drones-info", 1, (short) 1);
    }

}