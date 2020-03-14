package com.heima.admin.kafka;

import com.heima.common.kafka.KafkaListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaListener implements KafkaListener {
    @Override
    public String topic() {
        return "topic.test";
    }

    @Override
    public void onMessage(ConsumerRecord data, Consumer consumer) {
        System.out.println("===接收到的消息为===》"+data);
    }

    @Override
    public void onMessage(Object o) {

    }
}
