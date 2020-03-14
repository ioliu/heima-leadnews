package com.heima.admin.service.test;

import com.heima.admin.service.ReviewCrawlerArticleService;
import com.heima.admin.service.ReviewMediaArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReviewArticleTest {

    @Autowired
    private ReviewMediaArticleService reviewMediaArticleService;

    @Test
    public void testReview(){
        reviewMediaArticleService.autoReviewArticleByMedia(4101);
    }

    @Autowired
    private ReviewCrawlerArticleService reviewCrawlerArticleService;

    @Test
    public void testReviewCraeler() throws Exception {
        reviewCrawlerArticleService.autoReivewArticleByCrawler(35181);
    }
}
