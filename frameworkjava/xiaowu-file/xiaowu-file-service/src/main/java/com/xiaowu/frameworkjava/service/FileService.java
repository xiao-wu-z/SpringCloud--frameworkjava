package com.xiaowu.frameworkjava.service;

import com.xiaowu.frameworkjava.domain.dto.FileDTO;
import com.xiaowu.frameworkjava.domain.dto.SignDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileDTO upload(MultipartFile multipartFile);

    SignDTO getSign();
}
