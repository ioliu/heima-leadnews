package com.heima.images.service.impl;

import com.heima.common.fastdfs.FastDfsClient;
import com.heima.images.service.CacheImageService;
import com.heima.utils.common.Base64Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class CacheImageServiceImpl implements CacheImageService {

    @Autowired
    private FastDfsClient fastDfsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    final long EXPIRE = 60 * 60 * 24;//24消息

    @Override
    public byte[] cache2Redis(String imgUrl, boolean isCache) {
        byte[] ret = null;
        //http://192.168.25.133/group1/M00/00/01/wKgZhV2HdvSAF0ufAAGu97qOKoc956.jpg
        Map<String, String> map = formatPath(imgUrl);
        String group = map.get("group");
        String file = map.get("file");
        String baseString = "";
        try {
            ret = fastDfsClient.downGroupFile(group, file);
            baseString = Base64Utils.encode(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isCache) {
            redisTemplate.opsForValue().set(imgUrl, baseString, EXPIRE, TimeUnit.SECONDS);
        }
        return ret;
    }

    /**
     * 解析图片url 2 map
     *
     * @param imgUrl
     * @return
     */
    private Map<String, String> formatPath(String imgUrl) {
        //http://192.168.25.133/group1/M00/00/01/wKgZhV2HdvSAF0ufAAGu97qOKoc956.jpg
        Map<String, String> map = new HashMap<>();
        //group1/M00/00/01/wKgZhV2HdvSAF0ufAAGu97qOKoc956.jpg
        String groupString = imgUrl.substring(imgUrl.indexOf("group"), imgUrl.length());
        int index = groupString.indexOf("/");
        String group = groupString.substring(0, index);
        String file = groupString.substring(index + 1, groupString.length());
        map.put("group", group);
        map.put("file", file);
        return map;
    }

    @Override
    public void resetCache2Redis(String imageKey) {
        redisTemplate.expire(imageKey,EXPIRE,TimeUnit.SECONDS);
    }
}
