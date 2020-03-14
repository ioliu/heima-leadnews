package com.heima.media.service;

import com.heima.model.admin.pojos.AdChannel;

import java.util.List;

public interface AdChannelService {

    /**
     * 查询所有的频道
     * @return
     */
    List<AdChannel>  selectAll();
}
