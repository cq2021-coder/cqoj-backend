package com.cq.cqoj.exception;

import com.cq.cqoj.common.CommonResponse;
import com.cq.cqoj.common.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author 程崎
 * @since 2023/07/29
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResponse<?> businessExceptionHandler(BusinessException e) {
        log.info("BusinessException", e);
        if (e.getMessage() != null) {
            return CommonResponse.error(e.getResultCodeEnum(), e.getMessage());
        }
        return CommonResponse.error(e.getResultCodeEnum());
    }

    @ExceptionHandler(RuntimeException.class)
    public CommonResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return CommonResponse.error(ResultCodeEnum.SYSTEM_ERROR);
    }
}
