package com.heima.common.hbase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * HBase 配置类，读取 hbase.properties 配置文件
 */
@Setter
@Getter
@Configuration
@PropertySource("classpath:hbase.properties")
public class HBaseConfig {

    /**
     * hBase 注册地址
     */
    @Value("${hbase.zookeeper.quorum}")
    private String zookip_quorum;
    /**
     * 超时时间
     */
    @Value("${hbase.client.keyvalue.maxsize}")
    private String maxsize;

    /**
     * 创建HBaseClien
     *
     * @return
     */
    @Bean
    public HBaseClent getHBaseClient() {
        org.apache.hadoop.conf.Configuration hBaseConfiguration = getHbaseConfiguration();
        return new HBaseClent(hBaseConfiguration);
    }

    /**
     * 获取HbaseConfiguration 对象
     * @return
     */
    private org.apache.hadoop.conf.Configuration getHbaseConfiguration() {
        org.apache.hadoop.conf.Configuration hBaseConfiguration = new org.apache.hadoop.conf.Configuration();
        hBaseConfiguration.set("hbase.zookeeper.quorum", zookip_quorum);
        hBaseConfiguration.set("hbase.client.keyvalue.maxsize", maxsize);
        return hBaseConfiguration;
    }
}
