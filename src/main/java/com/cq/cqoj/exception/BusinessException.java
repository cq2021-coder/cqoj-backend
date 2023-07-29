package com.cq.cqoj.exception;


import com.cq.cqoj.common.ResultCodeEnum;
import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author 程崎
 * @since 2023/07/29
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final ResultCodeEnum resultCodeEnum;

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.resultCodeEnum = resultCodeEnum;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, String message) {
        super(message);
        this.resultCodeEnum = resultCodeEnum;
    }

}
