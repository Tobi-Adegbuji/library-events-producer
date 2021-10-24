package com.learnkafka.libraryeventsproducer.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local") //Only used when local is active
public class AutoCreateConfig {


    //CREATES A TOPIC IN KAFKA CLUSTER
    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name("library-events")
                .partitions(3)
                .replicas(3)
                .build();
    }


}
