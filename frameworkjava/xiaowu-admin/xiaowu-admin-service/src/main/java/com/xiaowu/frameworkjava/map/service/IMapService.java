package com.xiaowu.frameworkjava.map.service;

import com.xiaowu.frameworkjava.domain.vo.BasePageVO;
import com.xiaowu.frameworkjava.map.domain.dto.PlaceSearchReqDTO;
import com.xiaowu.frameworkjava.map.domain.dto.SearchPoiDTO;
import com.xiaowu.frameworkjava.map.domain.dto.SysRegionDTO;

import java.util.List;
import java.util.Map;

public interface IMapService {
    /**
     * 获取城市列表
     * @return
     */
    List<SysRegionDTO> getCityList();

    /**
     * 获取城市拼音列表
     * @return
     */
    Map<String, List<SysRegionDTO>> getCityPylist();

    /**
     * 根据父级区域ID获取子级区域列表
     * @param parentId 父级区域ID
     * @return 子级区域列表
     */
    List<SysRegionDTO> regionChildren(Long parentId);

    /**
     * 获取热门城市列表
     * @return 热门城市列表
     */
    List<SysRegionDTO> getHotCityList();

    /**
     * 根据地点搜索
     * @param placeSearchReqDTO
     * @return
     */
    BasePageVO<SearchPoiDTO> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO);
}
