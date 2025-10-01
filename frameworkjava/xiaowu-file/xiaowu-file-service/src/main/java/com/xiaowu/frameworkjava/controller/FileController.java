package com.xiaowu.frameworkjava.controller;


import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.domain.dto.FileDTO;
import com.xiaowu.frameworkjava.domain.dto.SignDTO;
import com.xiaowu.frameworkjava.domain.vo.FileVO;
import com.xiaowu.frameworkjava.domain.vo.SignVO;
import com.xiaowu.frameworkjava.service.impl.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
public class FileController {

    @Autowired
    private IFileService fileService;

    @PostMapping("/upload")
    public R<FileVO> uploadFile(MultipartFile multipartFile) {
        FileDTO fileDTO = fileService.upload(multipartFile);
        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(fileDTO, fileVO);
        return R.ok(fileVO);
    }

    @GetMapping("/sign")
    public R<SignVO> getSign() {
        SignDTO signDTO = fileService.getSign();
        SignVO signVO = new SignVO();
        BeanUtils.copyProperties(signDTO, signVO);
        return R.ok(signVO);
    }
}
