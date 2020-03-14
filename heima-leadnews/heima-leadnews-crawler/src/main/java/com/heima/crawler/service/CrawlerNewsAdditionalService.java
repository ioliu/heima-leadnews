package com.heima.crawler.service;

import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.pojos.ClNewsAdditional;

import java.util.Date;
import java.util.List;

public interface CrawlerNewsAdditionalService {

    /**
     * 保存
     * @param clNewsAdditional
     */
    public void saveAdditional(ClNewsAdditional clNewsAdditional);

    /**
     * 根据当前需要更新的时间查询列表
     * @param currentDate
     * @return
     */
    public List<ClNewsAdditional> queryListByNeedUpdate(Date currentDate);

    /**
     * 根据条件查询
     * @param clNewsAdditional
     * @return
     */
    public List<ClNewsAdditional> queryList(ClNewsAdditional clNewsAdditional);

    /**
     * 检查是否存在
     * @param url
     * @return
     */
    public boolean checkExist(String url);

    /**
     * 根据url获取图片附加信息
     * @param url
     * @return
     */
    public ClNewsAdditional getAdditionalByUrl(String url);

    /**
     * 是否是已存在的url
     * @param url
     * @return
     */
    public boolean isExistUrl(String url);

    /**
     * 更新
     * @param clNewsAdditional
     */
    public void updateAdditional(ClNewsAdditional clNewsAdditional);

    /**
     * 转换数据为parseItem
     * @param additionalList
     * @return
     */
    public List<ParseItem> toParseItem(List<ClNewsAdditional> additionalList);

    /**
     * 查询增量的统计数据
     * @param currentDate
     * @return
     */
    public List<ParseItem> queryIncrementParseItem(Date currentDate);

}
