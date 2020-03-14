package com.heima.media.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.StatisticDto;

public interface StatisticsService {
	/**  
	* 查找图文统计数据  
	* @param dto  
	* @return  
	*/  
	ResponseResult findWmNewsStatistics(StatisticDto dto);
}