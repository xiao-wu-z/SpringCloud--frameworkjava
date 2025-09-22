package com.xiaowu.frameworkjava.handler;

import com.xiaowu.frameworkjava.domain.R;
import com.xiaowu.frameworkjava.domain.ResultCode;
import com.xiaowu.frameworkjava.exception.ServiceException;
import com.xiaowu.frameworkjava.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关统一异常处理
 */
@Order(-1)
@Configuration
@Slf4j
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {


    /**
     * 处理器
     * @param exchange ServerWebExchange
     * @param ex 异常信息
     * @return 无
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        int retCode = ResultCode.ERROR_CODE.getCode();
        String retMsg = ResultCode.ERROR_CODE.getMsg();
        if (ex instanceof NoResourceFoundException) {
            retCode = ResultCode.SERVICE_NOT_FOUND.getCode();
            retMsg = ResultCode.SERVICE_NOT_FOUND.getMsg();
        } else if (ex instanceof ServiceException) {
            retCode = ((ServiceException) ex).getCode();
            retMsg = ex.getMessage();
        }

        int httpCode = Integer.parseInt(String.valueOf(retCode).substring(0, 3));

        log.error("[网关请求异常],请求路径为{},异常信息为{}", exchange.getRequest().getPath()
                , ex.getMessage());
        return webFluxResponseWriter(response,
                HttpStatus.valueOf(httpCode), retMsg, retCode);
    }

    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse
                                                            response, HttpStatus status,
                                                    Object value, int code) {
        return webFluxResponseWriter(response,
                MediaType.APPLICATION_JSON_VALUE, status, value, code);
    }
    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse
                                                            response,
                                                    String contentType,
                                                    HttpStatus status, Object value, int code) {
        response.setStatusCode(status); //设置http响应
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType); //设置响应体内容类型为json
        R<?> result = R.fail(code, value.toString()); //按照约定响应数据结构，构建响应体内容
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(JsonUtil.obj2String(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer)); //将响应体内容写⼊响应体
    }
}
