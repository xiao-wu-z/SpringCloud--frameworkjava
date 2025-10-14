package com.xiaowu.frameworkjava.map.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xiaowu.frameworkjava.domain.vo.BasePageVO;
import com.xiaowu.frameworkjava.map.constants.MapConstants;
import com.xiaowu.frameworkjava.map.domain.dto.*;
import com.xiaowu.frameworkjava.map.domain.entity.SysRegion;
import com.xiaowu.frameworkjava.map.mapper.RegionMapper;
import com.xiaowu.frameworkjava.map.service.IMapProvider;
import com.xiaowu.frameworkjava.map.service.IMapService;
import com.xiaowu.frameworkjava.service.RedisService;
import com.xiaowu.frameworkjava.utils.CacheUtil;
import com.xiaowu.frameworkjava.utils.PageUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


@Service
public class MapServiceImpl implements IMapService {

    /**
     * sys_region的mapper
     */
    @Autowired
    private RegionMapper regionMapper;

    /**
     * redis服务
     */
    @Autowired
    private RedisService redisService;

    /**
     *  caffeine缓存
     */
    @Autowired
    private Cache<String, Object> caffeineCache;

    /**
     * 地图服务提供者
     */
    @Autowired
    private IMapProvider mapProvider;


    @PostConstruct
    public void initCityMap() {
        List<SysRegion> regionList = regionMapper.selectAllRegion();
        // 在服务加载之前缓存城市列表数据
        loadCityInfo(regionList);

        // 3 在服务启动期间，缓存城市归类列表
        loadCityPinyinInfo(regionList);
    }

    private void loadCityPinyinInfo(List<SysRegion> regionList) {
        Map<String, List<SysRegionDTO>> cityMap = new TreeMap<>();

        for(SysRegion region : regionList) {
            if (region.getLevel().equals(MapConstants.CITY_LEVEL)) {
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                String firstChar = regionDTO.getPinyin().substring(0, 1).toUpperCase();

                if (cityMap.containsKey(firstChar)) {
                    cityMap.get(firstChar).add(regionDTO);
                }else {
                    List<SysRegionDTO> regionDTOList = new ArrayList<>();
                    regionDTOList.add(regionDTO);
                    cityMap.put(firstChar, regionDTOList);
                }
            }
        }
        CacheUtil.setL2Cache(redisService, MapConstants.CACHE_MAP_CITY_PINYIN_KEY,
                cityMap, caffeineCache, 120L, TimeUnit.MINUTES);
    }

    private void loadCityInfo(List<SysRegion> regionList) {
        List<SysRegionDTO> regionDTOList = new ArrayList<>();

        for (SysRegion region : regionList) {
            if (region.getLevel().equals(MapConstants.CITY_LEVEL)) {
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                regionDTOList.add(regionDTO);
            }
        }
        CacheUtil.setL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY,
                regionDTOList, caffeineCache, 120L, TimeUnit.MINUTES);
    }

    /**
     * 获取城市列表V1方案 -- 直接查询数据库
     * @return 列表数据
     */
    public List<SysRegionDTO> getCityListV1() {
        List<SysRegionDTO> regionDTOList = new ArrayList<>();

        List<SysRegion> regionList = regionMapper.selectAllRegion();
        for (SysRegion region : regionList) {
            if (region.getLevel().equals(MapConstants.CITY_LEVEL)) {
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                regionDTOList.add(regionDTO);
            }
        }
        return regionDTOList;
    }

    /**
     * 获取城市列表V2方案 -- 先查询Redis缓存，Redis缓存中没有再查询数据库
     * @return 列表数据
     */
    public List<SysRegionDTO> getCityListV2() {
        List<SysRegionDTO> regionDTOList = new ArrayList<>();

        regionDTOList = redisService.getCacheObject(MapConstants.CACHE_MAP_CITY_KEY,
                new TypeReference<List<SysRegionDTO>>() {});
        if (regionDTOList != null) {
            return regionDTOList;
        }
        List<SysRegion> regionList = regionMapper.selectAllRegion();
        for (SysRegion region : regionList) {
            if (region.getLevel().equals(MapConstants.CITY_LEVEL)) {
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                regionDTOList.add(regionDTO);
            }
        }
        redisService.setCacheObject(MapConstants.CACHE_MAP_CITY_KEY, regionDTOList);
        return regionDTOList;
    }

    /**
     * 获取城市列表V3方案 -- 引入二级缓存
     * @return 列表数据
     */
    public List<SysRegionDTO> getCityListV3() {
        List<SysRegionDTO> regionDTOList = new ArrayList<>();

        regionDTOList = CacheUtil.getL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY,
                new TypeReference<List<SysRegionDTO>>() {}, caffeineCache);

        if (regionDTOList != null) {
            return regionDTOList;
        }
        List<SysRegion> regionList = regionMapper.selectAllRegion();
        for (SysRegion region : regionList) {
            if (region.getLevel().equals(MapConstants.CITY_LEVEL)) {
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                regionDTOList.add(regionDTO);
            }
        }
        CacheUtil.setL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY, regionDTOList,
                caffeineCache, 120L, TimeUnit.MINUTES);

        return regionDTOList;
    }

    /**
     * 获取城市列表V4方案 -- 缓存预热方案
     * @return 缓存列表数据
     */
    @Override
    public List<SysRegionDTO> getCityList() {
        List<SysRegionDTO> result = CacheUtil.getL2Cache(redisService,
                MapConstants.CACHE_MAP_CITY_KEY,
                new TypeReference<List<SysRegionDTO>>() {}, caffeineCache);
        return result;
    }

    @Override
    public Map<String, List<SysRegionDTO>> getCityPylist() {
        Map<String, List<SysRegionDTO>> result = CacheUtil.getL2Cache(redisService,
                MapConstants.CACHE_MAP_CITY_PINYIN_KEY,
                new TypeReference<Map<String, List<SysRegionDTO>>>() {}, caffeineCache);
        return result;

    }

    /**
     * 获取指定父级ID下的所有子区域信息
     * @param parentId 父级区域ID
     * @return 返回子区域DTO列表
     */
    @Override
    public List<SysRegionDTO> regionChildren(Long parentId) {
        // 构建缓存键，使用父级ID和常量组合
        String key = MapConstants.CACHE_MAP_CITY_CHILDREN_KEY + parentId;

        // 尝试从二级缓存中获取数据
        List<SysRegionDTO> result = CacheUtil.getL2Cache(redisService, key,
                new TypeReference<List<SysRegionDTO>>() {}, caffeineCache);

        // 如果缓存中存在数据，直接返回
        if (result != null) {
            return result;
        }
        // 从数据库中查询所有区域信息
        List<SysRegion> regionList = regionMapper.selectAllRegion();
        // 初始化结果列表
        result = new ArrayList<>();
        // 遍历区域列表，筛选出属于指定父级ID的子区域
        for (SysRegion region : regionList) {
            if (region.getParentId() != null && region.getParentId().equals(parentId)) {
                // 创建DTO对象并复制属性
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                // 添加到结果列表
                result.add(regionDTO);
            }
        }
        // 将查询结果存入二级缓存，设置缓存时间为120分钟
        CacheUtil.setL2Cache(redisService, key, result,
                caffeineCache, 120L, TimeUnit.MINUTES);
        // 返回结果列表
        return result;
    }

    @Override
    public List<SysRegionDTO> getHotCityList() {
        List<SysRegionDTO> result = CacheUtil.getL2Cache(redisService,
                MapConstants.CACHE_MAP_HOT_CITY,
                new TypeReference<List<SysRegionDTO>>() {}, caffeineCache);
        if (result != null) {
            return result;
        }

        List<SysRegion> regionList = regionMapper.selectAllRegion();
        String ids = "110100,310100,120100,440100,330100,370100,320100";
        for (String id : ids.split(",")) {
            for (SysRegion region : regionList) {
                if (region.getId().equals(Long.valueOf(id))) {
                    SysRegionDTO regionDTO = new SysRegionDTO();
                    BeanUtils.copyProperties(region, regionDTO);
                    result.add(regionDTO);
                }
            }
        }
        CacheUtil.setL2Cache(redisService, MapConstants.CACHE_MAP_HOT_CITY, result,
                caffeineCache, 120L, TimeUnit.MINUTES);
        return result;

    }

/**
 * 根据地区搜索地点建议并返回分页结果
 * @param placeSearchReqDTO 地点搜索请求参数
 * @return 返回包含搜索结果的分页视图对象
 */
    @Override
    public BasePageVO<SearchPoiDTO> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO) {
    // 将请求参数转换为搜索建议DTO
        SuggestSearchDTO suggestSearchDTO = new SuggestSearchDTO();
        BeanUtils.copyProperties(placeSearchReqDTO, suggestSearchDTO);
    // 设置页码和ID参数
        suggestSearchDTO.setPageIndex(placeSearchReqDTO.getPageNo());
        suggestSearchDTO.setId(String.valueOf(placeSearchReqDTO.getId()));

    // 调用地图服务提供商搜索地点
        PoiListDTO poiListDTO = mapProvider.searchQQMapPlaceByRegion(suggestSearchDTO);
        List<PoiDTO> poiDTOList = poiListDTO.getData();

    // 初始化分页结果对象
        BasePageVO<SearchPoiDTO> result = new BasePageVO<>();
    // 设置总记录数和总页数
        result.setTotals(poiListDTO.getCount());
        result.setTotalPages(PageUtil.getTotalPages(result.getTotals(),
                placeSearchReqDTO.getPageSize()));

    // 处理搜索结果列表
        List<SearchPoiDTO> pageRes = new ArrayList<>();
        for (PoiDTO poiDTO : poiDTOList) {
        // 将PoiDTO转换为SearchPoiDTO
            SearchPoiDTO searchPoiDTO = new SearchPoiDTO();
            BeanUtils.copyProperties(poiDTO, searchPoiDTO);
        // 设置经纬度信息
            searchPoiDTO.setLongitude(poiDTO.getLocation().getLng());
            searchPoiDTO.setLatitude(poiDTO.getLocation().getLat());
            pageRes.add(searchPoiDTO);
        }
        result.setList(pageRes);
        return result;

    }
}
