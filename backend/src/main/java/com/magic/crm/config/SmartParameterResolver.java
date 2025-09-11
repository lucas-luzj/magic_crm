package com.magic.crm.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 智能参数解析器
 * 自动从JSON body、URL参数、表单数据中解析方法参数
 * 无需使用@RequestParam、@RequestBody等注解
 * 
 * 注意：此解析器会排除以下注解的参数，让它们由对应的专门解析器处理：
 * - @AuthenticationPrincipal: 由Spring
 * Security的AuthenticationPrincipalArgumentResolver处理
 * - @RequestBody: 由RequestResponseBodyMethodProcessor处理
 * - @RequestParam: 由RequestParamMethodArgumentResolver处理
 * - @PathVariable: 由PathVariableMethodArgumentResolver处理
 * - @RequestHeader: 由RequestHeaderMethodArgumentResolver处理
 * - @CookieValue: 由ServletCookieValueMethodArgumentResolver处理
 * - @RequestAttribute: 由RequestAttributeMethodArgumentResolver处理
 * - @SessionAttribute: 由SessionAttributeMethodArgumentResolver处理
 */
public class SmartParameterResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(SmartParameterResolver.class);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 排除Spring内置类型和特殊类型
        Class<?> paramType = parameter.getParameterType();

        // 不处理这些特殊类型
        if (Authentication.class.isAssignableFrom(paramType) ||
                HttpServletRequest.class.isAssignableFrom(paramType) ||
                MultipartFile.class.isAssignableFrom(paramType) ||
                paramType.getName().startsWith("org.springframework") ||
                paramType.getName().startsWith("jakarta.servlet")) {
            return false;
        }

        // 不处理带有特定注解的参数，这些参数应该由对应的解析器处理
        if (parameter.hasParameterAnnotation(AuthenticationPrincipal.class) || // Spring Security
                parameter.hasParameterAnnotation(RequestBody.class) || // JSON body
                parameter.hasParameterAnnotation(RequestParam.class) || // URL/form 参数
                parameter.hasParameterAnnotation(PathVariable.class) || // 路径变量
                parameter.hasParameterAnnotation(RequestHeader.class) || // 请求头
                parameter.hasParameterAnnotation(CookieValue.class) || // Cookie
                parameter.hasParameterAnnotation(RequestAttribute.class) || // 请求属性
                parameter.hasParameterAnnotation(SessionAttribute.class)) { // 会话属性
            return false;
        }

        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Class<?> paramType = parameter.getParameterType();
        String paramName = parameter.getParameterName();

        // logger.debug("Resolving parameter: {} of type: {}", paramName,
        // paramType.getSimpleName());

        // 获取所有可能的数据源
        Map<String, Object> allParams = getAllParameters(request);

        // logger.debug("All parameters: {}", allParams);

        // 如果是基本类型或包装类型
        if (isSimpleType(paramType)) {
            Object value = convertSimpleType(allParams.get(paramName), paramType);
            // logger.debug("Resolved simple parameter {} = {}", paramName, value);
            return value;
        }

        // 如果是复杂对象类型
        Object value = convertComplexType(allParams, paramType, paramName);
        // logger.debug("Resolved complex parameter {} = {}", paramName, value);
        return value;
    }

    /**
     * 获取所有参数（JSON body + URL参数 + 表单参数）
     */
    private Map<String, Object> getAllParameters(HttpServletRequest request) throws Exception {
        Map<String, Object> allParams = new HashMap<>();

        // 1. 先获取URL参数和表单参数
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values.length == 1) {
                allParams.put(key, values[0]);
            } else {
                allParams.put(key, Arrays.asList(values));
            }
        }

        // 2. 再获取JSON body参数（优先级更高）
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            try {
                // 使用更可靠的方式读取请求体
                String jsonBody = getRequestBody(request);
                // logger.debug("JSON body: {}", jsonBody);

                if (jsonBody != null && !jsonBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(jsonBody);
                    Map<String, Object> jsonParams = objectMapper.convertValue(jsonNode, Map.class);
                    // logger.debug("Parsed JSON params: {}", jsonParams);
                    allParams.putAll(jsonParams); // JSON参数覆盖URL参数
                }
            } catch (Exception e) {
                logger.warn("Failed to parse JSON body: {}", e.getMessage());
            }
        }

        return allParams;
    }

    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            // 检查是否已经有缓存的请求体
            Object cachedBody = request.getAttribute("CACHED_REQUEST_BODY");
            if (cachedBody != null) {
                return cachedBody.toString();
            }

            // 读取请求体
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            // 缓存请求体，避免重复读取
            request.setAttribute("CACHED_REQUEST_BODY", body);

            return body;
        } catch (IOException e) {
            logger.warn("Failed to read request body: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否为简单类型
     */
    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                type == Integer.class || type == int.class ||
                type == Long.class || type == long.class ||
                type == Double.class || type == double.class ||
                type == Float.class || type == float.class ||
                type == Boolean.class || type == boolean.class ||
                type == Byte.class || type == byte.class ||
                type == Short.class || type == short.class ||
                type == Character.class || type == char.class ||
                Date.class.isAssignableFrom(type) ||
                Number.class.isAssignableFrom(type);
    }

    /**
     * 转换简单类型
     */
    private Object convertSimpleType(Object value, Class<?> targetType) {
        if (value == null) {
            return getDefaultValue(targetType);
        }

        String strValue = value.toString();

        if (targetType == String.class) {
            return strValue;
        } else if (targetType == Integer.class || targetType == int.class) {
            return strValue.isEmpty() ? 0 : Integer.parseInt(strValue);
        } else if (targetType == Long.class || targetType == long.class) {
            return strValue.isEmpty() ? 0L : Long.parseLong(strValue);
        } else if (targetType == Double.class || targetType == double.class) {
            return strValue.isEmpty() ? 0.0 : Double.parseDouble(strValue);
        } else if (targetType == Float.class || targetType == float.class) {
            return strValue.isEmpty() ? 0.0f : Float.parseFloat(strValue);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(strValue);
        }

        return value;
    }

    /**
     * 转换复杂类型
     */
    private Object convertComplexType(Map<String, Object> allParams, Class<?> targetType, String paramName)
            throws Exception {
        // 如果参数名对应的值是一个完整的对象
        Object directValue = allParams.get(paramName);
        if (directValue != null && directValue instanceof Map) {
            return objectMapper.convertValue(directValue, targetType);
        }

        // 否则尝试从所有参数中构建对象
        try {
            Object instance = targetType.getDeclaredConstructor().newInstance();
            Field[] fields = targetType.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = allParams.get(fieldName);

                if (fieldValue != null) {
                    if (isSimpleType(field.getType())) {
                        field.set(instance, convertSimpleType(fieldValue, field.getType()));
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        // 处理List类型
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType paramType = (ParameterizedType) genericType;
                            Class<?> elementType = (Class<?>) paramType.getActualTypeArguments()[0];

                            if (fieldValue instanceof List) {
                                List<Object> list = new ArrayList<>();
                                for (Object item : (List<?>) fieldValue) {
                                    if (isSimpleType(elementType)) {
                                        list.add(convertSimpleType(item, elementType));
                                    } else {
                                        list.add(objectMapper.convertValue(item, elementType));
                                    }
                                }
                                field.set(instance, list);
                            }
                        }
                    } else {
                        // 处理嵌套对象
                        Object nestedObject = objectMapper.convertValue(fieldValue, field.getType());
                        field.set(instance, nestedObject);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            // 如果构建失败，尝试直接转换
            return objectMapper.convertValue(allParams, targetType);
        }
    }

    /**
     * 获取基本类型的默认值
     */
    private Object getDefaultValue(Class<?> type) {
        if (type == int.class)
            return 0;
        if (type == long.class)
            return 0L;
        if (type == double.class)
            return 0.0;
        if (type == float.class)
            return 0.0f;
        if (type == boolean.class)
            return false;
        if (type == byte.class)
            return (byte) 0;
        if (type == short.class)
            return (short) 0;
        if (type == char.class)
            return '\0';
        return null;
    }
}