package com.xiaowu.frameworkjava.map.service;

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
}
