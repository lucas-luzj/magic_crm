package com.magic.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.crm.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应包装器
 * 自动将Controller返回的数据包装成ApiResponse格式
 */
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 只处理我们自己的Controller，排除Spring Boot内置的端点
        String packageName = returnType.getDeclaringClass().getPackageName();
        return packageName.startsWith("com.magic.crm.controller");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        var contentType = response.getHeaders().getContentType();
        if (contentType != null) {
            return body;
        }
        // 如果已经是ApiResponse类型，直接返回
        if (body instanceof ApiResponse) {
            return body;
        }

        // 如果是String类型，需要特殊处理（避免Jackson序列化问题）
        if (body instanceof String) {
            try {
                ApiResponse<String> apiResponse = ApiResponse.success("操作成功", (String) body);
                return objectMapper.writeValueAsString(apiResponse);
            } catch (Exception e) {
                return body;
            }
        }

        // 对于void方法（返回null），包装成成功响应
        if (body == null) {
            return ApiResponse.success("操作成功");
        }

        // 其他类型包装成ApiResponse
        return ApiResponse.success("操作成功", body);
    }
}