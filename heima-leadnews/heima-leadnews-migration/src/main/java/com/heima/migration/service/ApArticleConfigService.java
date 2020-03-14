package com.heima.migration.service;

import com.heima.model.article.pojos.ApArticleConfig;

import java.util.List;

public interface ApArticleConfigService {

    public List<ApArticleConfig> queryByArticleIds(List<String> ids);

    public ApArticleConfig getByArticleId(Integer id);
}
