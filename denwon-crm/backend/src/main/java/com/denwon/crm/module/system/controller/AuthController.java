package com.denwon.crm.module.system.controller;

import com.denwon.crm.common.response.ApiResponse;
import com.denwon.crm.module.system.dto.LoginRequest;
import com.denwon.crm.module.system.dto.LoginResponse;
import com.denwon.crm.module.system.service.AuthService;
import com.denwon.crm.module.system.service.UserService;
import com.denwon.crm.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, 
                                           HttpServletRequest httpRequest) {
        try {
            String ip = IpUtils.getIpAddress(httpRequest);
            LoginResponse response = authService.login(request, ip);
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            userService.recordLoginFailure(request.getUsername());
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = authService.refreshToken(request.getRefreshToken());
            return ApiResponse.success("Token刷新成功", response);
        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            return ApiResponse.error(401, "Token刷新失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return ApiResponse.error("登出失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ApiResponse<LoginResponse.UserInfo> getCurrentUser() {
        try {
            LoginResponse.UserInfo userInfo = authService.getCurrentUserInfo();
            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request.getOldPassword(), request.getNewPassword());
            return ApiResponse.success("密码修改成功", null);
        } catch (Exception e) {
            log.error("密码修改失败: {}", e.getMessage());
            return ApiResponse.error("密码修改失败: " + e.getMessage());
        }
    }
    
    @Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 8, message = "密码长度至少8位")
        private String newPassword;
    }
    
    @Data
    public static class RefreshTokenRequest {
        @NotBlank(message = "刷新Token不能为空")
        private String refreshToken;
    }
}