package com.heima.admin.kafka.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaTest {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Test
    public void test(){
        try {
            kafkaTemplate.send("topic.test","123KEY","itcast...");
            System.out.println("==========消息发送了============");
            Thread.sleep(5000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
