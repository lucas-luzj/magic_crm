package com.magic.crm.config;

import com.magic.crm.interceptor.WorkflowPermissionInterceptor;
import lombok.RequiredArgsConstructor;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器和其他Web相关设置
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WorkflowPermissionInterceptor workflowPermissionInterceptor;

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的静态资源映射，启用目录浏览
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // 配置静态资源映射
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加工作流权限拦截器
        registry.addInterceptor(workflowPermissionInterceptor)
                .addPathPatterns("/api/workflow/**")
                .excludePathPatterns(
                        "/api/workflow/test/**", // 排除测试接口
                        "/api/auth/**", // 排除认证接口
                        "/api/public/**" // 排除公共接口
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 添加智能参数解析器
        resolvers.add(new SmartParameterResolver());
    }
}