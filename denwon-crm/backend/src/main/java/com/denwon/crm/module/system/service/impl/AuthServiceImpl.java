package com.denwon.crm.module.system.service.impl;

import com.denwon.crm.common.exception.BusinessException;
import com.denwon.crm.module.system.dto.LoginRequest;
import com.denwon.crm.module.system.dto.LoginResponse;
import com.denwon.crm.module.system.entity.User;
import com.denwon.crm.module.system.service.AuthService;
import com.denwon.crm.module.system.service.UserService;
import com.denwon.crm.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    
    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ip) {
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // 设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            User user = (User) authentication.getPrincipal();
            
            // 更新登录信息
            userService.updateLoginInfo(user.getId(), ip);
            
            // 生成Token
            String accessToken = jwtUtils.generateToken(user.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());
            
            // 构建用户信息
            LoginResponse.UserInfo userInfo = buildUserInfo(user);
            
            return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .userInfo(userInfo)
                .build();
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            throw new BusinessException("用户名或密码错误");
        }
    }
    
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            // 验证刷新Token
            if (!jwtUtils.validateToken(refreshToken)) {
                throw new BusinessException("无效的刷新Token");
            }
            
            // 提取用户名
            String username = jwtUtils.extractUsername(refreshToken);
            
            // 获取用户信息
            User user = userService.getUserByUsername(username);
            
            // 生成新的Token
            String newAccessToken = jwtUtils.generateToken(username);
            String newRefreshToken = jwtUtils.generateRefreshToken(username);
            
            // 构建用户信息
            LoginResponse.UserInfo userInfo = buildUserInfo(user);
            
            return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .userInfo(userInfo)
                .build();
        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            throw new BusinessException("刷新Token失败");
        }
    }
    
    @Override
    public void logout(String token) {
        // 清除安全上下文
        SecurityContextHolder.clearContext();
        
        // TODO: 如果需要，可以将token加入黑名单
        log.info("用户已登出");
    }
    
    @Override
    public LoginResponse.UserInfo getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        User user = (User) authentication.getPrincipal();
        return buildUserInfo(user);
    }
    
    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        
        User user = (User) authentication.getPrincipal();
        userService.changePassword(user.getId(), oldPassword, newPassword);
    }
    
    /**
     * 构建用户信息
     */
    private LoginResponse.UserInfo buildUserInfo(User user) {
        // 获取角色
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getCode())
            .collect(Collectors.toSet());
        
        // 获取权限
        Set<String> permissions = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(auth -> !auth.startsWith("ROLE_"))
            .collect(Collectors.toSet());
        
        // 构建组织信息
        LoginResponse.OrgUnitInfo orgUnitInfo = null;
        if (user.getOrgUnit() != null) {
            orgUnitInfo = LoginResponse.OrgUnitInfo.builder()
                .id(user.getOrgUnit().getId())
                .code(user.getOrgUnit().getCode())
                .name(user.getOrgUnit().getName())
                .type(user.getOrgUnit().getType())
                .build();
        }
        
        return LoginResponse.UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .email(user.getEmail())
            .mobile(user.getMobile())
            .avatar(user.getAvatar())
            .title(user.getTitle())
            .orgUnit(orgUnitInfo)
            .roles(roles)
            .permissions(permissions)
            .build();
    }
}