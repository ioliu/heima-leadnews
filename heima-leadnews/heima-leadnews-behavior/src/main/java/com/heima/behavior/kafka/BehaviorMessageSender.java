package com.heima.behavior.kafka;

import com.heima.common.kafka.KafkaMessage;
import com.heima.common.kafka.KafkaSender;
import com.heima.common.kafka.messages.UpdateArticleMessage;
import com.heima.common.kafka.messages.behavior.UserLikesMessage;
import com.heima.common.kafka.messages.behavior.UserReadMessage;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.mess.app.UpdateArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BehaviorMessageSender {

    @Autowired
    private KafkaSender kafkaSender;

    /**
     * 发送+1的消息
     *
     * @param message
     * @param apUserId
     * @param isSendToArticle
     */
    public void sendMessagePlus(KafkaMessage message, Long apUserId, boolean isSendToArticle) {
        if(isSendToArticle){
            UpdateArticleMessage temp = parseMessage(message, apUserId, 1);
            kafkaSender.sendArticleUpdateBus(temp);
        }
    }

    /**
     * 发送-1的消息
     *
     * @param message
     * @param apUserId
     * @param isSendToArticle
     */
    public void sendMessageReduce(KafkaMessage message, Long apUserId, boolean isSendToArticle) {
        if(isSendToArticle){
            UpdateArticleMessage temp = parseMessage(message, apUserId, -1);
            kafkaSender.sendArticleUpdateBus(temp);
        }
    }

    private UpdateArticleMessage parseMessage(KafkaMessage message, Long apUserId, int step) {
        UpdateArticle ua = new UpdateArticle();
        if(apUserId!=null){
            ua.setApUserId(apUserId.intValue());
        }
        if(message instanceof UserLikesMessage){
            UserLikesMessage likesMessage = (UserLikesMessage) message;
            //只处理文章数据的点赞
            if(likesMessage.getData().getType() == ApLikesBehavior.Type.ARTICLE.getCode()){
                ua.setType(UpdateArticle.UpdateArticleType.LIKES);
                ua.setAdd(step);
                ua.setArticleId(likesMessage.getData().getEntryId());
                ua.setBehaviorId(likesMessage.getData().getBehaviorEntryId());
            }

        }else if(message instanceof UserReadMessage){
            UserReadMessage userReadMessage = (UserReadMessage) message;
            ua.setType(UpdateArticle.UpdateArticleType.VIEWS);
            ua.setAdd(step);
            ua.setArticleId(userReadMessage.getData().getArticleId());
            ua.setBehaviorId(userReadMessage.getData().getEntryId());
        }

        if(ua.getArticleId()!=null){
            return new UpdateArticleMessage(ua);
        }
        return null;

    }
}
