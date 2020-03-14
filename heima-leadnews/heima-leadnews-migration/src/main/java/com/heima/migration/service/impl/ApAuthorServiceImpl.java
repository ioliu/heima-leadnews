package com.heima.migration.service.impl;

import com.heima.migration.service.ApAuthorService;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.mappers.app.ApAuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class ApAuthorServiceImpl implements ApAuthorService {

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Override
    public List<ApAuthor> queryByIds(List<Integer> ids) {
        return apAuthorMapper.selectByIds(ids);
    }

    @Override
    public ApAuthor getById(Long id) {
        if(null != id){
            return apAuthorMapper.selectById(id.intValue());
        }
        return null;
    }
}
