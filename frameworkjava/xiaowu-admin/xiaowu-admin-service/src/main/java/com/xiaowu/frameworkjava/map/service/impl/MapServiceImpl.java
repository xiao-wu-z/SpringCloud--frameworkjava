package com.xiaowu.frameworkjava.map.service.impl;

import com.xiaowu.frameworkjava.map.constants.MapConstants;
import com.xiaowu.frameworkjava.map.domain.dto.SysRegionDTO;
import com.xiaowu.frameworkjava.map.domain.entity.SysRegion;
import com.xiaowu.frameworkjava.map.mapper.RegionMapper;
import com.xiaowu.frameworkjava.map.service.IMapService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class MapServiceImpl implements IMapService {

    /**
     * sys_region的mapper
     */
    @Autowired
    private RegionMapper regionMapper;

    @Override
    public List<SysRegionDTO> getCityList() {
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
}
