package com.heima.common.kafka.messages;

import com.heima.common.kafka.KafkaMessage;
import com.heima.model.mess.app.UpdateArticle;

public class UpdateArticleMessage extends KafkaMessage<UpdateArticle> {

    public UpdateArticleMessage(){}

    public UpdateArticleMessage(UpdateArticle data){
        super(data);
    }

    @Override
    public String getType() {
        return "update-article";
    }
}
