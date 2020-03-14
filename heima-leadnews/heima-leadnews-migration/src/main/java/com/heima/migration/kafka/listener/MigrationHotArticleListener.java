package com.heima.migration.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.common.kafka.KafkaListener;
import com.heima.common.kafka.KafkaTopicConfig;
import com.heima.common.kafka.messages.app.ApHotArticleMessage;
import com.heima.migration.service.ApHotArticleService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class MigrationHotArticleListener implements KafkaListener<String,String> {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApHotArticleService apHotArticleService;

    @Override
    public String topic() {
        return kafkaTopicConfig.getHotArticle();
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Consumer<?, ?> consumer) {
        log.info("接收到热点文章数据:{}",data);
        String value = data.value();
        if(null != value){
            ApHotArticleMessage message = null;
            try {
                message = mapper.readValue(value, ApHotArticleMessage.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Integer articleId = message.getData().getArticleId();
            if(null != articleId){
                apHotArticleService.hotApArticleSync(articleId);
            }
        }
    }
}
