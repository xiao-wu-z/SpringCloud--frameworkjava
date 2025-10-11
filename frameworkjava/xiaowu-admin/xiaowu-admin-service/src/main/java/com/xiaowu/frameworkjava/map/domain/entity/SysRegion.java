package com.xiaowu.frameworkjava.map.domain.entity;

import com.xiaowu.frameworkjava.domain.entity.BaseDO;
import lombok.Data;

@Data
public class SysRegion extends BaseDO {
    /**
     * 区域名称
     */
    private String name;

    /**
     * 区域全称
     */
    private String fullName;

    /**
     * 父级区域ID
     */
    private Long parentId;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 区域编码
     */
    private String code;

    /**
     * 父级区域编码
     */
    private String parentCode;
}
