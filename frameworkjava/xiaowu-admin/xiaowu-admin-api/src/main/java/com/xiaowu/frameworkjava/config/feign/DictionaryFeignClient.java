package com.xiaowu.frameworkjava.config.feign;


import org.springframework.cloud.openfeign.FeignClient;


/**
 * 字典服务相关远程调用
 */
@FeignClient(contextId = "dictionaryFeignClient", value = "xiaowu-admin")
public interface DictionaryFeignClient {
}
