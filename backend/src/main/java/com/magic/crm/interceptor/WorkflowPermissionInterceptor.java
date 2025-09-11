package com.magic.crm.interceptor;

import com.magic.crm.service.FlowablePermissionService;
import com.magic.crm.service.UserService;
import com.magic.crm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 工作流权限拦截器
 * 用于检查用户对流程和任务的操作权限
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowPermissionInterceptor implements HandlerInterceptor {

    private final FlowablePermissionService permissionService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 获取用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"未授权访问\"}");
            return false;
        }

        try {
            // 流程定义权限检查
            if (uri.contains("/api/workflow/process-definitions")) {
                return checkProcessDefinitionPermission(uri, method, userId, response);
            }

            // 流程实例权限检查
            if (uri.contains("/api/workflow/process-instances")) {
                return checkProcessInstancePermission(uri, method, userId, response);
            }

            // 任务权限检查
            if (uri.contains("/api/workflow/tasks")) {
                return checkTaskPermission(uri, method, userId, request, response);
            }

            return true;

        } catch (Exception e) {
            log.error("权限检查失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"权限检查失败\"}");
            return false;
        }
    }

    /**
     * 检查流程定义权限
     */
    private boolean checkProcessDefinitionPermission(String uri, String method, Long userId,
            HttpServletResponse response) throws Exception {
        String permission;

        if ("GET".equals(method)) {
            permission = "READ";
        } else if ("POST".equals(method) && uri.contains("/deploy")) {
            permission = "MANAGE";
        } else if ("PUT".equals(method) || "DELETE".equals(method)) {
            permission = "MANAGE";
        } else {
            permission = "READ";
        }

        if (!permissionService.hasProcessPermission(userId, null, permission)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"没有" + permission + "权限\"}");
            return false;
        }

        return true;
    }

    /**
     * 检查流程实例权限
     */
    private boolean checkProcessInstancePermission(String uri, String method, Long userId, HttpServletResponse response)
            throws Exception {
        String permission;

        if ("GET".equals(method)) {
            permission = "READ";
        } else if ("POST".equals(method) && uri.contains("/start")) {
            permission = "START";
        } else if ("PUT".equals(method) || "DELETE".equals(method)) {
            permission = "MANAGE";
        } else {
            permission = "READ";
        }

        if (!permissionService.hasProcessPermission(userId, null, permission)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"没有" + permission + "权限\"}");
            return false;
        }

        return true;
    }

    /**
     * 检查任务权限
     */
    private boolean checkTaskPermission(String uri, String method, Long userId, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // 对于任务列表查询，只检查基本权限
        if ("GET".equals(method) && (uri.endsWith("/pending") || uri.endsWith("/completed"))) {
            return true; // 用户可以查看自己的任务
        }

        // 对于具体任务操作，需要检查任务权限
        String taskId = extractTaskIdFromUri(uri);
        if (taskId != null) {
            if (!permissionService.hasTaskPermission(userId, taskId, "EXECUTE")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"没有操作此任务的权限\"}");
                return false;
            }
        }

        return true;
    }

    /**
     * 从URI中提取任务ID
     */
    private String extractTaskIdFromUri(String uri) {
        // URI格式: /api/workflow/tasks/{taskId}/...
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("tasks".equals(parts[i]) && i + 1 < parts.length) {
                String taskId = parts[i + 1];
                // 检查是否是有效的任务ID（不是操作名称）
                if (!taskId.equals("pending") && !taskId.equals("completed")) {
                    return taskId;
                }
            }
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null && jwtUtil.validateToken(token)) {
                return jwtUtil.extractUserId(token);
            }
        } catch (Exception e) {
            log.warn("获取用户ID失败", e);
        }
        return null;
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}