package com.heima.images.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.common.kafka.KafkaListener;
import com.heima.common.kafka.KafkaTopicConfig;
import com.heima.common.kafka.messages.app.ApHotArticleMessage;
import com.heima.images.service.HotArticleImageService;
import javafx.scene.chart.XYChart;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class HotArticleListener implements KafkaListener<String,String> {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private HotArticleImageService hotArticleImageService;

    @Override
    public String topic() {
        return kafkaTopicConfig.getHotArticle();
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Consumer<?, ?> consumer) {
        log.info("接收到热文章消息为:{}", data);
        String value = data.value();
        try {
            ApHotArticleMessage message = mapper.readValue(value, ApHotArticleMessage.class);
            hotArticleImageService.handleHotImage(message);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("接收消息失败，错误消息:{}",e);
        }
    }
}
