package com.xiaowu.frameworkjava.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeListReqDTO;
import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeWriteReqDTO;
import com.xiaowu.frameworkjava.config.domain.entity.SysDictionaryType;
import com.xiaowu.frameworkjava.config.domain.vo.DictionaryTypeVO;
import com.xiaowu.frameworkjava.config.mapper.SysDictionaryTypeMapper;
import com.xiaowu.frameworkjava.config.service.ISysDictionaryService;
import com.xiaowu.frameworkjava.domain.vo.BasePageVO;
import com.xiaowu.frameworkjava.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysDictionaryTypeServiceImpl implements ISysDictionaryService {

    @Autowired
    private SysDictionaryTypeMapper sysDictionaryTypeMapper;

    @Override
    public Long addType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) {
        LambdaQueryWrapper<SysDictionaryType> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.select(SysDictionaryType::getId)
                .eq(SysDictionaryType::getValue, dictionaryTypeWriteReqDTO.getValue()).or()
                .eq(SysDictionaryType::getTypeKey, dictionaryTypeWriteReqDTO.getTypeKey());
        SysDictionaryType sysDictionaryType = sysDictionaryTypeMapper
                .selectOne(lambdaQueryWrapper);
        if (sysDictionaryType != null) {
            throw new ServiceException("字典类型已存在");
        }
        sysDictionaryType = new SysDictionaryType();
        sysDictionaryType.setValue(dictionaryTypeWriteReqDTO.getValue());
        sysDictionaryType.setTypeKey(dictionaryTypeWriteReqDTO.getTypeKey());
        if (StringUtils.isNotBlank(dictionaryTypeWriteReqDTO.getRemark())) {
            sysDictionaryType.setRemark(dictionaryTypeWriteReqDTO.getRemark());
        }
        sysDictionaryTypeMapper.insert(sysDictionaryType);
        return sysDictionaryType.getId();
    }

    @Override
    public BasePageVO<DictionaryTypeVO> listType(DictionaryTypeListReqDTO dictionaryTypeListReqDTO) {
        BasePageVO<DictionaryTypeVO> result = new BasePageVO<>();
        LambdaQueryWrapper<SysDictionaryType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dictionaryTypeListReqDTO.getTypeKey())) {
            lambdaQueryWrapper.eq(SysDictionaryType::getTypeKey, dictionaryTypeListReqDTO.getTypeKey());
        }
        if (StringUtils.isNotBlank(dictionaryTypeListReqDTO.getValue())) {
            lambdaQueryWrapper.likeRight(SysDictionaryType::getValue, dictionaryTypeListReqDTO.getValue());
        }
        Page<SysDictionaryType> page = sysDictionaryTypeMapper
                .selectPage(new Page<>(dictionaryTypeListReqDTO.getPageNo().longValue(),
                        dictionaryTypeListReqDTO.getPageSize().longValue()),
                        lambdaQueryWrapper);
        result.setTotals(Integer.parseInt(String.valueOf(page.getTotal())));
        result.setTotalPages(Integer.parseInt(String.valueOf(page.getPages())));
        for (SysDictionaryType sysDictionaryType : page.getRecords()) {
            DictionaryTypeVO dictionaryTypeVO = new DictionaryTypeVO();
            BeanUtils.copyProperties(sysDictionaryType, dictionaryTypeVO);
            result.getList().add(dictionaryTypeVO);
        }
        return result;
    }
}
