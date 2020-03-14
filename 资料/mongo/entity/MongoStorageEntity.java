package com.heima.common.mongo.entity;

import com.heima.common.common.storage.StorageEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongoDB 存储实体
 * Document 是指表明是什么
 */
@Document(collection = "mongo_storage_data")
@Setter
@Getter
public class MongoStorageEntity extends StorageEntity {
    /**
     * 主键的Key
     *
     * @Id 标明该字段是主键
     */
    @Id
    private String rowKey;

}
