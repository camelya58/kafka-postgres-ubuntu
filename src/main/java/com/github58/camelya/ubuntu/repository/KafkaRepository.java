package com.github58.camelya.ubuntu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Class KafkaRepository represents the process of sending message to kafka topic.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@RequiredArgsConstructor
@Repository
public class KafkaRepository {

    @Value("${spring.kafka.template.default-topic}")
    private String kafkaTopic;


    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void sendMessage(Long msgId, Object data) {
        ListenableFuture<SendResult<Long, Object>> future = kafkaTemplate.send(kafkaTopic, msgId, data);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }
}
