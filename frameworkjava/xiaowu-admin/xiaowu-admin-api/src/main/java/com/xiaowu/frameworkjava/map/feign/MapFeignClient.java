package com.xiaowu.frameworkjava.map.feign;

import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.map.domain.vo.RegionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "mapFeignClient", value = "xiaowu-admin")
public interface MapFeignClient {
    /**
     * 城市列表查询
     * @return 城市列表信息
     */
    @GetMapping("/map/city_list")
    R<List<RegionVO>> getCityList();

    /**
     * 城市拼音归类查询
     * @return 城市字母与城市列表的哈希
     */
    @GetMapping("/map/city_pinyin_list")
    R<Map<String, List<RegionVO>>> getCityPylist();
}
