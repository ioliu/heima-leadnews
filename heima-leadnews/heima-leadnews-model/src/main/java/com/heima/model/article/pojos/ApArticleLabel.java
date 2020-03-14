package com.heima.model.article.pojos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApArticleLabel {

    public ApArticleLabel(Integer articleId, Integer labelId) {
        this.articleId = articleId;
        this.labelId = labelId;
    }

    public ApArticleLabel() {
    }

    private Integer id;

    private Integer articleId;

    private Integer labelId;

    private Integer count;
}