package com.xiaowu.frameworkjava.map.service.impl;

import com.xiaowu.frameworkjava.domain.ResultCode;
import com.xiaowu.frameworkjava.exception.ServiceException;
import com.xiaowu.frameworkjava.map.constants.MapConstants;
import com.xiaowu.frameworkjava.map.domain.dto.GeoResultDTO;
import com.xiaowu.frameworkjava.map.domain.dto.LocationDTO;
import com.xiaowu.frameworkjava.map.domain.dto.PoiListDTO;
import com.xiaowu.frameworkjava.map.domain.dto.SuggestSearchDTO;
import com.xiaowu.frameworkjava.map.service.IMapProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Slf4j
@Component
@RefreshScope
@ConditionalOnProperty(value = "map.type", havingValue = "qqmap")
public class QQMapServiceImpl implements IMapProvider {

    /**
     * 腾讯位置服务域名
     */
    @Value("${qqmap.apiServer}")
    private String apiServer;

    /**
     * 调用腾讯位置服务的秘钥
     */
    @Value("${qqmap.key}")
    private String key;

    @Autowired
    private RestTemplate restTemplate;




/**
 * 根据地区搜索QQ地图地点的方法
 *
 * @param suggestSearchDTO 搜索条件的数据传输对象，包含地区和其他搜索参数
 * @return PoiListDTO 包含搜索结果的地点列表数据传输对象，当前实现返回null
 */
    @Override
    public PoiListDTO searchQQMapPlaceByRegion(SuggestSearchDTO suggestSearchDTO) {
        // 1 构建请求url
        String url = String.format(
                apiServer + MapConstants.QQMAP_API_PLACE_SUGGESTION +
                        "?key=%s&region=%s&region_fix=1&page_index=%s&page_size=%s&keyword=%s",
                key, suggestSearchDTO.getId(), suggestSearchDTO.getPageIndex(),
                suggestSearchDTO.getPageSize(),suggestSearchDTO.getKeyword()
        );
        // 2 直接发送请求，并拿到返回结果再做对象转换
        ResponseEntity<PoiListDTO> response =  restTemplate.getForEntity(url, PoiListDTO.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("获取关键词查询结果异常", response);
            throw new ServiceException(ResultCode.QQMAP_QUERY_FAILED);
        }
        return response.getBody();
    }


    /**
     * 根据经纬度获取区域信息
     * @param locationDTO 经纬度
     * @return 区域信息
     */
    @Override
    public GeoResultDTO getQQMapDistrictByLonLat(LocationDTO locationDTO) {
        // 1 构建请求url
        String url = String.format(apiServer + MapConstants.QQMAP_GEOCODER +
                        "?key=%s&location=%s",
                key, locationDTO.formatInfo()
        );
        // 2 直接发送请求，并拿到返回结果再做对象转换
        ResponseEntity<GeoResultDTO> response =  restTemplate.getForEntity(url,
                GeoResultDTO.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("根据经纬度来获取区域信息查询结果异常", response);
            throw new ServiceException(ResultCode.QQMAP_QUERY_FAILED);
        }
        return response.getBody();
    }
}
