package com.xiaowu.frameworkjava.map.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xiaowu.frameworkjava.map.constants.MapConstants;
import com.xiaowu.frameworkjava.map.domain.dto.SysRegionDTO;
import com.xiaowu.frameworkjava.map.domain.entity.SysRegion;
import com.xiaowu.frameworkjava.map.mapper.RegionMapper;
import com.xiaowu.frameworkjava.map.service.IMapService;
import com.xiaowu.frameworkjava.service.RedisService;
import com.xiaowu.frameworkjava.utils.CacheUtil;
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

    @Override
    public List<SysRegionDTO> regionChildren(Long parentId) {
        String key = MapConstants.CACHE_MAP_CITY_KEY + parentId;

        List<SysRegionDTO> result = CacheUtil.getL2Cache(redisService, key,
                new TypeReference<List<SysRegionDTO>>() {}, caffeineCache);

        if (result != null) {
            return result;
        }
        List<SysRegion> regionList = regionMapper.selectAllRegion();
        for (SysRegion region : regionList) {
            if (region.getParentId() != null && region.getParentId().equals(parentId)) {
                SysRegionDTO regionDTO = new SysRegionDTO();
                BeanUtils.copyProperties(region, regionDTO);
                result.add(regionDTO);
            }
        }
        CacheUtil.setL2Cache(redisService, key, result,
                caffeineCache, 120L, TimeUnit.MINUTES);
        return result;
    }
}
