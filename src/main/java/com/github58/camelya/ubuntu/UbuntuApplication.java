package com.github58.camelya.ubuntu;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@SpringBootApplication
public class UbuntuApplication {

    @KafkaListener(topics = "test-topic")
    public void messageListener(ConsumerRecord<Long, Object> record) {
        System.out.println(record.partition());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.topic());
    }

    public static void main(String[] args) {
        SpringApplication.run(UbuntuApplication.class, args);
    }

}
