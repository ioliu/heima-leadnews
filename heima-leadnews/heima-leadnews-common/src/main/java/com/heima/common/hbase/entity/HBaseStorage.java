package com.heima.common.hbase.entity;

import com.heima.common.common.storage.StorageData;
import com.heima.common.common.storage.StorageEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hbase 存储对象 继承 StorageEntity
 * 用于存储各种对象
 */
@Setter
@Getter
public class HBaseStorage extends StorageEntity {
    /**
     * 主键key
     *
     */
    private String rowKey;
    /**
     * Hbase 的回调接口，用于将回调方法
     */
    private HBaseInvok hBaseInvok;

    /**
     * 获取类簇数组
     * @return
     */
    public List<String> getColumnFamily() {
        return getDataList().stream().map(StorageData::getTargetClassName).collect(Collectors.toList());
    }

    /**
     * 进行回调
     */
    public void invok() {
        if (null != hBaseInvok) {
            hBaseInvok.invok();
        }
    }
}