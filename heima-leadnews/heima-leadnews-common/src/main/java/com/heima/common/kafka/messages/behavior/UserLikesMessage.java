package com.heima.common.kafka.messages.behavior;

import com.heima.common.kafka.KafkaMessage;
import com.heima.model.behavior.pojos.ApLikesBehavior;

public class UserLikesMessage extends KafkaMessage<ApLikesBehavior> {

    public UserLikesMessage(){}

    public UserLikesMessage(ApLikesBehavior data){
        super(data);
    }

    @Override
    public String getType() {
        return "user-likes";
    }
}
