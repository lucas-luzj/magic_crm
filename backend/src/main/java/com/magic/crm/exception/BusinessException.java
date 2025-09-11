package com.magic.crm.exception;

/**
 * 业务异常类
 * 用于业务逻辑中抛出的异常，会被全局异常处理器捕获并返回友好的错误信息
 */
public class BusinessException extends RuntimeException {
    
    private final int code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }
    
    public int getCode() {
        return code;
    }
}