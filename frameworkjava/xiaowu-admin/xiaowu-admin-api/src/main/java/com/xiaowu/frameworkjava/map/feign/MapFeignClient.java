package com.xiaowu.frameworkjava.map.feign;

import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.map.domain.vo.RegionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(contextId = "mapFeignClient", value = "xiaowu-admin")
public interface MapFeignClient {
    /**
     * 城市列表查询
     * @return 城市列表信息
     */
    @GetMapping("/map/city_list")
    R<List<RegionVO>> getCityList();
}
