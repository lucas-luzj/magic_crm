package com.denwon.crm.common.exception;

/**
 * 业务异常
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }
    
    public Integer getCode() {
        return code;
    }
}