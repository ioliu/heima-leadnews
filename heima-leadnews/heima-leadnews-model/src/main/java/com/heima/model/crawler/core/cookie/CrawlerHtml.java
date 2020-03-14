package com.heima.model.crawler.core.cookie;


import com.heima.model.crawler.core.proxy.CrawlerProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class CrawlerHtml {

    public CrawlerHtml() {
    }

    public CrawlerHtml(String url) {
        this.url = url;
    }


    private String url;

    private String html;

    private CrawlerProxy proxy;

    private List<CrawlerCookie> crawlerCookieList = null;

}
