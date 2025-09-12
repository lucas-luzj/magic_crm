package com.denwon.crm.module.system.service;

import com.denwon.crm.module.system.dto.LoginRequest;
import com.denwon.crm.module.system.dto.LoginResponse;

/**
 * 认证服务接口
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request, String ip);
    
    /**
     * 刷新Token
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 用户登出
     */
    void logout(String token);
    
    /**
     * 获取当前用户信息
     */
    LoginResponse.UserInfo getCurrentUserInfo();
    
    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);
}