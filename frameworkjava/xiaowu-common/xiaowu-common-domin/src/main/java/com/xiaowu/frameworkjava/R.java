package com.xiaowu.frameworkjava;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class R <T>{
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功返回
     * @return 结果
     * @param <T> 数据类型
     */
    public static <T> R<T> ok() {
        R<T> result = new R<T>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(null);
        return result;
    }

    /**
     * 成功返回
     * @param data 返回数据
     * @return 结果
     * @param <T> 数据类型
     */
    public static <T> R<T> ok(T data) {
        R<T> result = new R<T>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    /**
     * 成功返回
     * @param msg 返回信息
     * @param data 返回数据
     * @return 结果
     * @param <T> 数据类型
     */
    public static <T> R<T> ok(String msg, T data) {
        R<T> result = new R<T>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回
     * @return 返回失败结果
     * @param <T> 数据类型
     */
    public static <T> R<T> fail() {
        return restResult(ResultCode.ERROR, null);
    }

    /**
     * 失败返回
     * @param msg 失败信息
     * @return 返回失败结果
     * @param <T>  数据类型
     */
    public static <T> R<T> fail(String msg) {
        return restResult(ResultCode.ERROR.getCode(), msg, null);
    }

    /**
     * 失败返回
     * @param code 失败码
     * @param msg 失败信息
     * @return 返回失败结果
     * @param <T> 数据类型
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(code, msg, null);
    }

    /**
     * 失败返回
     * @param data 失败数据
     * @return 返回失败结果
     * @param <T> 数据类型
     */
    public static <T> R<T> fail(T data) {
        return restResult(ResultCode.ERROR, data);
    }


    /**
     * 自定义返回
     * @param resultCode 返回码
     * @param data 返回数据
     * @return
     * @param <T> 数据类型
     */
    public static <T> R<T> restResult(ResultCode resultCode, T data) {
        R<T> result = new R<T>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(data);
        return result;
    }

    /**
     * 自定义返回
     * @param code 返回码
     * @param msg 返回信息
     * @param data 返回数据
     * @return 返回结果
     * @param <T> 数据类型
     */
    public static <T> R<T> restResult(int code , String msg, T data) {
        R<T> result = new R<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 自定义返回
     * @param resultCode 状态
     * @return 返回结果
     * @param <T> 数据类型
     */
    public static <T> R<T> restResult(ResultCode resultCode) {
        R<T> result = new R<T>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(null);
        return result;
    }

    /**
     * 自定义返回
     * @param code 状态码
     * @param msg 状态信息
     * @return 返回结果
     * @param <T> 数据类型
     */
    public static <T> R<T> restResult(int code, String msg) {
        R<T> result = new R<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}
