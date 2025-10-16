package com.xiaowu.frameworkjava.config.service;

import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeListReqDTO;
import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeWriteReqDTO;
import com.xiaowu.frameworkjava.config.domain.vo.DictionaryTypeVO;
import com.xiaowu.frameworkjava.domain.vo.BasePageVO;

public interface ISysDictionaryService {
    Long addType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO);

    BasePageVO<DictionaryTypeVO> listType(DictionaryTypeListReqDTO dictionaryTypeListReqDTO);
}
