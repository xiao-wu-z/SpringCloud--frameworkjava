package com.xiaowu.frameworkjava.map.controller;

import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.domain.vo.BasePageVO;
import com.xiaowu.frameworkjava.map.domain.dto.PlaceSearchReqDTO;
import com.xiaowu.frameworkjava.map.domain.dto.SearchPoiDTO;
import com.xiaowu.frameworkjava.map.domain.dto.SysRegionDTO;
import com.xiaowu.frameworkjava.map.domain.vo.RegionVO;
import com.xiaowu.frameworkjava.map.domain.vo.SearchPoiVO;
import com.xiaowu.frameworkjava.map.feign.MapFeignClient;
import com.xiaowu.frameworkjava.map.service.IMapService;
import com.xiaowu.frameworkjava.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    @Override
    public R<Map<String, List<RegionVO>>> getCityPylist() {
        Map<String, List<SysRegionDTO>> pinyinList  = mapService.getCityPylist();

        Map<String, List<RegionVO>> result = new LinkedHashMap<>();

        for (Map.Entry<String, List<SysRegionDTO>> entry : pinyinList.entrySet()) {
            result.put(entry.getKey(),
                    BeanUtil.copyListProperties(entry.getValue(), RegionVO::new));
        }
        return R.ok(result);
    }

    @Override
    public R<List<RegionVO>> regionChildren(Long parentId) {
        List<SysRegionDTO> regionList = mapService.regionChildren(parentId);

        List<RegionVO> regionVOS = BeanUtil.copyListProperties(regionList, RegionVO::new);

        return R.ok(regionVOS);
    }

    @Override
    public R<List<RegionVO>> getHotCityList() {
        List<SysRegionDTO> regionList = mapService.getHotCityList();

        List<RegionVO> regionVOS = BeanUtil.copyListProperties(regionList, RegionVO::new);
        return R.ok(regionVOS);
    }

    @Override
    public R<BasePageVO<SearchPoiVO>> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO) {
        BasePageVO<SearchPoiDTO> basePageReqDTO  = mapService.searchSuggestOnMap(placeSearchReqDTO);

        BasePageVO<SearchPoiVO> result = new BasePageVO<>();
        BeanUtils.copyProperties(basePageReqDTO, result);
        return R.ok(result);

    }
}
