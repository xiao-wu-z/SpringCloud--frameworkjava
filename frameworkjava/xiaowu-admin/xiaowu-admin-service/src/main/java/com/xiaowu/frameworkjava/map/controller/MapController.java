package com.xiaowu.frameworkjava.map.controller;

import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.map.domain.dto.SysRegionDTO;
import com.xiaowu.frameworkjava.map.domain.vo.RegionVO;
import com.xiaowu.frameworkjava.map.feign.MapFeignClient;
import com.xiaowu.frameworkjava.map.service.IMapService;
import com.xiaowu.frameworkjava.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 地图相关控制器类
 */
@RestController
@Slf4j
public class MapController implements MapFeignClient {

    @Autowired
    private IMapService mapService;


    @Override
    public R<List<RegionVO>> getCityList() {
        List<SysRegionDTO> regionList = mapService.getCityList();
        List<RegionVO> regionVOS = BeanUtil.copyListProperties(regionList, RegionVO::new);
        return R.ok(regionVOS);
    }
}
