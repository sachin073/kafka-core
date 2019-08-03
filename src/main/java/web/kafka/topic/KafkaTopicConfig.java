package web.kafka.topic;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrap.servers}")
    private String bootstrapAddress;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    final public static String twitterTopic="twitter-feed";
    final public static String testTopic="twitter-feed";

    @Bean
    public NewTopic twitterFeed() {
        return new NewTopic(twitterTopic, 3, (short) 2);
    }

    @Bean
    public NewTopic SampleTopic() {
        return new NewTopic(testTopic, 3, (short) 2);
    }

}
