package com.heima.common.kafka.messages.app;

import com.heima.common.kafka.KafkaMessage;
import com.heima.model.mess.app.ArticleVisitStreamDto;

public class ArticleVisitStreamMessage extends KafkaMessage<ArticleVisitStreamDto> {

    public ArticleVisitStreamMessage(){}

    public ArticleVisitStreamMessage(ArticleVisitStreamDto data){
        super(data);
    }

    @Override
    public String getType() {
        return "article-visit-stream";
    }
}
