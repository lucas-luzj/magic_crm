package com.magic.crm.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Bean属性复制工具类
 * 提供多种对象属性复制的方案
 */
public class BeanCopyUtils {

    /**
     * 方案1: 使用Spring BeanUtils (推荐)
     * 优点: 简单易用，性能好，Spring自带
     * 缺点: 只能复制同名同类型的属性
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("属性复制失败", e);
        }
    }

    /**
     * 方案2: 使用Spring BeanUtils，忽略指定属性
     */
    public static <T> T copyPropertiesIgnore(Object source, Class<T> targetClass, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target, ignoreProperties);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("属性复制失败", e);
        }
    }

    /**
     * 方案3: 使用反射手动复制 (灵活但性能较差)
     * 优点: 可以自定义复制逻辑
     * 缺点: 性能较差，代码复杂
     */
    public static <T> T copyPropertiesManual(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();

            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = targetClass.getDeclaredFields();

            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                if (value != null) {
                    // 查找目标对象中同名的字段
                    Field targetField = Arrays.stream(targetFields)
                            .filter(f -> f.getName().equals(sourceField.getName()))
                            .filter(f -> f.getType().equals(sourceField.getType()))
                            .findFirst()
                            .orElse(null);

                    if (targetField != null) {
                        targetField.setAccessible(true);
                        targetField.set(target, value);
                    }
                }
            }

            return target;
        } catch (Exception e) {
            throw new RuntimeException("手动属性复制失败", e);
        }
    }

    /**
     * 方案4: 复制到已存在的对象
     */
    public static void copyPropertiesToExisting(Object source, Object target) {
        if (source != null && target != null) {
            BeanUtils.copyProperties(source, target);
        }
    }

    /**
     * copyBean方法 - 为了兼容现有代码
     */
    public static <T> T copyBean(Object source, Class<T> targetClass) {
        return copyProperties(source, targetClass);
    }

    /**
     * 方案5: 复制到已存在的对象，忽略null值
     */
    public static void copyPropertiesToExistingIgnoreNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        try {
            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = target.getClass().getDeclaredFields();

            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                // 只复制非null值
                if (value != null) {
                    Field targetField = Arrays.stream(targetFields)
                            .filter(f -> f.getName().equals(sourceField.getName()))
                            .filter(f -> f.getType().equals(sourceField.getType()))
                            .findFirst()
                            .orElse(null);

                    if (targetField != null) {
                        targetField.setAccessible(true);
                        targetField.set(target, value);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("属性复制失败", e);
        }
    }
}