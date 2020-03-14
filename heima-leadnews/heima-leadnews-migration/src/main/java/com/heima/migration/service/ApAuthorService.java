package com.heima.migration.service;

import com.heima.model.article.pojos.ApAuthor;

import java.util.List;

public interface ApAuthorService {

    public List<ApAuthor> queryByIds(List<Integer> ids);

    public ApAuthor getById(Long id);
}
