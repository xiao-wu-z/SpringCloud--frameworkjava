package com.xiaowu.frameworkjava;


import com.aliyun.oss.OSSClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;


/**
 * 阿里云oss 自动配置类
 */
@Configuration
@ConditionalOnProperty(value = "storage.type", havingValue = "oss")
public class OSSAutoConfiguration {

    /**
     * 阿里云oss 客户端
     */
    private OSSClient ossClient;
}
