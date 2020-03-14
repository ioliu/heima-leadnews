package com.heima.migration.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.common.kafka.KafkaListener;
import com.heima.common.kafka.KafkaTopicConfig;
import com.heima.common.kafka.messages.ArticleAuditSuccessMessage;
import com.heima.migration.service.ArticleQuantityService;
import com.heima.model.mess.admin.ArticleAuditSuccess;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class MigrationAuditSucessArticleListener implements KafkaListener<String,String> {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ArticleQuantityService articleQuantityService;

    @Override
    public String topic() {
        return kafkaTopicConfig.getArticleAuditSuccess();
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Consumer<?, ?> consumer) {
        log.info("接收到审核消息:{}",data);
        String value = data.value();
        if(null != value){
            ArticleAuditSuccessMessage message = null;
            try {
                message = mapper.readValue(value, ArticleAuditSuccessMessage.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArticleAuditSuccess auditSuccess = message.getData();
            if(null != auditSuccess){
                Integer articleId = auditSuccess.getArticleId();
                if(null != articleId){
                    articleQuantityService.dbToHbase(articleId);
                }
            }
        }
    }
}
