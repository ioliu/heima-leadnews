package com.heima.common.kafka.messages.behavior;

import com.heima.common.kafka.KafkaMessage;
import com.heima.model.behavior.pojos.ApReadBehavior;

public class UserReadMessage extends KafkaMessage<ApReadBehavior> {

    public UserReadMessage(){}

    public UserReadMessage(ApReadBehavior data){
        super(data);
    }

    @Override
    public String getType() {
        return "user-read";
    }
}
