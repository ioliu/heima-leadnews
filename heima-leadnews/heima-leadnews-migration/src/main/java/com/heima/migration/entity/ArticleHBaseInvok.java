package com.heima.migration.entity;

import com.heima.common.hbase.entity.HBaseInvok;
import com.heima.model.article.pojos.ApArticle;
import lombok.Getter;
import lombok.Setter;

/**
 * 回调对象
 */
@Setter
@Getter
public class ArticleHBaseInvok implements HBaseInvok {

    /**
     * 回调需要传输的对象
     */
    private ApArticle apArticle;
    /**
     * 回调需要对应的回调接口
     */
    private ArticleCallBack articleCallBack;

    public ArticleHBaseInvok(ApArticle apArticle, ArticleCallBack articleCallBack) {
        this.apArticle = apArticle;
        this.articleCallBack = articleCallBack;
    }

    /**
     * 执行回调方法
     */
    @Override
    public void invok() {
        if (null != apArticle && null != articleCallBack) {
            articleCallBack.callBack(apArticle);
        }
    }
}