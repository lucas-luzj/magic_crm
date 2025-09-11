package com.magic.crm.service;

import com.magic.crm.dto.AuthResponse;
import com.magic.crm.entity.User;
import com.magic.crm.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(String username, String password, HttpServletRequest request) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User authenticatedUserEntity = (User) authenticatedUser;

        // 生成JWT token
        String token = jwtUtil.generateToken(authenticatedUserEntity);
        String refreshToken = jwtUtil.generateRefreshToken(authenticatedUserEntity);

        // 获取客户端IP和UserAgent
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // 更新最后登录时间、IP和UserAgent
        userService.updateLastLoginInfo(authenticatedUserEntity.getId(), clientIp, userAgent);

        return new AuthResponse(token, refreshToken, authenticatedUserEntity);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null && !xForwardedProto.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedProto)) {
            return xForwardedProto;
        }

        return request.getRemoteAddr();
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        User user = (User) userDetails;

        String newToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return new AuthResponse(newToken, newRefreshToken, user);
    }
}