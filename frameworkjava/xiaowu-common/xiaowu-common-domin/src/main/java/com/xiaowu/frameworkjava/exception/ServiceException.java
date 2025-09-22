package com.xiaowu.frameworkjava.exception;


import com.xiaowu.frameworkjava.domain.ResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常模板
 */
@Getter
@Setter
public class ServiceException extends RuntimeException{

    /**
     * 异常码
     */
    private int code;

    /**
     * 异常信息
     */
    private String msg;

    public ServiceException(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(String msg){
        this.msg = msg;
        this.code = ResultCode.ERROR_CODE.getCode();
    }

    public ServiceException(ResultCode resultCode){
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }
}
