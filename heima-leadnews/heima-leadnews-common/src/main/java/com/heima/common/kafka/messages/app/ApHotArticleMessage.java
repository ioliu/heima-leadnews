package com.heima.common.kafka.messages.app;

import com.heima.common.kafka.KafkaMessage;
import com.heima.model.article.pojos.ApHotArticles;

public class ApHotArticleMessage extends KafkaMessage<ApHotArticles> {

    public ApHotArticleMessage(){}

    public ApHotArticleMessage(ApHotArticles data){
        super(data);
    }

    @Override
    public String getType() {
        return "hot-article";
    }
}
