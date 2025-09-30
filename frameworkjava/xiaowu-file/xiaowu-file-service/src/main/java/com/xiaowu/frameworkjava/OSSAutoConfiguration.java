package com.xiaowu.frameworkjava;


import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
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


    /**
     * 获取阿里云oss客户端
     * @param prop 阿里云 oss 配置信息
     * @return 阿里云oss客户端
     */
    @Bean
    public OSSClient ossClient(OSSProperties prop) {
        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(
                prop.getAccessKeyId(), prop.getAccessKeySecret());

        // 创建ClientBuilderConfiguration
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignVersion.V4);

        // 使用内网endpoint进行上传 prop.getIntEndpoint()
        if (prop.getInternal()){
            ossClient = (OSSClient) OSSClientBuilder.create()
                    .endpoint(prop.getIntEndpoint())
                    .region(prop.getRegion())
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(conf)
                    .build();
        } else {
            ossClient = (OSSClient) OSSClientBuilder.create()
                    .endpoint(prop.getEndpoint())
                    .region(prop.getRegion())
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(conf)
                    .build();
        }

        return ossClient;
    }

    /**
     * 关闭客户端
     */
    @PreDestroy
    public void closeOSSClient() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
