package com.heima.article.controller.v2;

import com.heima.article.apis.ArticleHomeControllerApi;
import com.heima.article.service.AppArticleService;
import com.heima.common.article.constans.ArticleConstans;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/article")
public class ArticleHomeV2Controller implements ArticleHomeControllerApi {

    @Autowired
    private AppArticleService appArticleService;

    @Override
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {
        return appArticleService.loadV2(ArticleConstans.LOADTYPE_LOAD_MORE,dto,true);
    }

    @Override
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {
        return appArticleService.loadV2(ArticleConstans.LOADTYPE_LOAD_MORE,dto,false);
    }

    @Override
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto) {
        return appArticleService.loadV2(ArticleConstans.LOADTYPE_LOAD_NEW,dto,false);
    }
}
