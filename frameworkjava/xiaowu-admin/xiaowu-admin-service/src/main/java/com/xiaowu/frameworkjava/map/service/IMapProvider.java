package com.xiaowu.frameworkjava.map.service;

import com.xiaowu.frameworkjava.map.domain.dto.GeoResultDTO;
import com.xiaowu.frameworkjava.map.domain.dto.LocationDTO;
import com.xiaowu.frameworkjava.map.domain.dto.PoiListDTO;
import com.xiaowu.frameworkjava.map.domain.dto.SuggestSearchDTO;

/**
 * 地图服务提供者
 */
public interface IMapProvider {

    /**
     * 根据关键词搜索地点
     * @param suggestSearchDTO 搜索条件
     * @return 搜索结果
     */
    PoiListDTO searchQQMapPlaceByRegion(SuggestSearchDTO suggestSearchDTO);

    /**
     * 根据经纬度来获取区域信息
     * @param locationDTO 经纬度
     * @return 区域信息
     */
    GeoResultDTO getQQMapDistrictByLonLat(LocationDTO locationDTO);
}
