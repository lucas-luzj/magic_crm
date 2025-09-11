package com.magic.crm.security;

import com.magic.crm.service.UserService;
import com.magic.crm.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                logger.error("无法获取JWT Token中的用户名", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 直接从JWT token中获取用户ID，然后从数据库获取完整用户信息
            Long userId = jwtUtil.extractUserId(jwtToken);
            if (userId != null) {
                // 使用findByUsername获取完整的User实体，而不是loadUserByUsername
                com.magic.crm.entity.User user = userService.findByUsername(username).orElse(null);
                
                if (user != null) {
                    System.out.println("JwtFilter Debug - User ID: " + user.getId() + ", Username: " + user.getUsername());
                    
                    if (jwtUtil.validateToken(jwtToken, user)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,
                                null, user.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }
}