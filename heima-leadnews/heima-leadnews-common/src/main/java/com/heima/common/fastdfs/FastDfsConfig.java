package com.heima.common.fastdfs;

import com.luhuiguo.fastdfs.FdfsAutoConfiguration;
import com.luhuiguo.fastdfs.FdfsProperties;
import com.luhuiguo.fastdfs.conn.FdfsConnectionPool;
import com.luhuiguo.fastdfs.conn.PooledConnectionFactory;
import com.luhuiguo.fastdfs.conn.TrackerConnectionManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

/**
 * 自动化配置核心数据库的连接配置
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="fast.dfs")
@PropertySource("classpath:fast-dfs.properties")
public class FastDfsConfig extends FdfsAutoConfiguration {

    int soTimeout;
    int connectTimeout;
    String trackerServer;

    public FastDfsConfig(FdfsProperties properties){
        super(properties);
    }

    @Bean
    @Override
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setSoTimeout(getSoTimeout());
        pooledConnectionFactory.setConnectTimeout(getConnectTimeout());
        return pooledConnectionFactory;
    }


    @Bean
    @Override
    public TrackerConnectionManager trackerConnectionManager(FdfsConnectionPool fdfsConnectionPool) {
        return new TrackerConnectionManager(fdfsConnectionPool, Arrays.asList(trackerServer));
    }
}