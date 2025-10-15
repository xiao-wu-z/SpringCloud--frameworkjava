package com.xiaowu.frameworkjava.config.service;

import com.xiaowu.frameworkjava.config.domain.dto.DictionaryTypeWriteReqDTO;

public interface ISysDictionaryService {
    Long addType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO);
}
