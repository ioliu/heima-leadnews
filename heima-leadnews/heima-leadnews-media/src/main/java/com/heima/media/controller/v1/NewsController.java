package com.heima.media.controller.v1;

import com.heima.common.media.constans.WmMediaConstans;
import com.heima.media.apis.NewsControllerApi;
import com.heima.media.service.NewsService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmNewsDto;
import com.heima.model.media.dtos.WmNewsPageReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.PastOrPresent;

@RestController
@RequestMapping("/api/v1/media/news")
public class NewsController implements NewsControllerApi {

    @Autowired
    private NewsService newsService;

    @Override
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {
        return newsService.saveNews(dto, WmMediaConstans.WM_NEWS_SUMMIT_STATUS);
    }

    @Override
    @PostMapping("/save_draft")
    public ResponseResult saveDraftNews(@RequestBody WmNewsDto dto) {
        return newsService.saveNews(dto, WmMediaConstans.WM_NEWS_DRAFT_STATUS);
    }

    @Override
    @PostMapping("/list")
    public ResponseResult listByUser(@RequestBody WmNewsPageReqDto dto) {
        return newsService.listByUser(dto);
    }

    @Override
    @PostMapping("/news")
    public ResponseResult wmNews(@RequestBody WmNewsDto dto) {
        return newsService.findWmNewsById(dto);
    }

    @Override
    @PostMapping("/del_news")
    public ResponseResult delNews(@RequestBody WmNewsDto dto) {
        return newsService.delNews(dto);
    }
}
