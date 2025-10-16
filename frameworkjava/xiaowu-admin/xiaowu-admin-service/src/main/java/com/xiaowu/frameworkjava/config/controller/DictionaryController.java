package com.xiaowu.frameworkjava.config.controller;

import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeListReqDTO;
import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeWriteReqDTO;
import com.xiaowu.frameworkjava.config.domain.vo.DictionaryTypeVO;
import com.xiaowu.frameworkjava.config.feign.DictionaryFeignClient;
import com.xiaowu.frameworkjava.config.service.ISysDictionaryService;
import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.domain.vo.BasePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 字典服务相关接口
 */
@RestController
public class DictionaryController implements DictionaryFeignClient {

    @Autowired
    private ISysDictionaryService iSysDictionaryService;

    /**
     * 新增字典类型
     * @param dictionaryTypeWriteReqDTO 新增字典类型DTO
     * @return Long
     */
    @PostMapping("/dictionary_type/add")
    public R<Long> addType(@RequestBody @Validated DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) {
        return R.ok(iSysDictionaryService.addType(dictionaryTypeWriteReqDTO));
    }


    /**
     * 字典类型列表
     * @param dictionaryTypeListReqDTO 字典类型列表DTO
     * @return BasePageVO
     */
    @GetMapping("/dictionary_type/list")
    public R<BasePageVO<DictionaryTypeVO>> listType(@Validated DictionaryTypeListReqDTO dictionaryTypeListReqDTO) {
        return R.ok(iSysDictionaryService.listType(dictionaryTypeListReqDTO));
    }
}
