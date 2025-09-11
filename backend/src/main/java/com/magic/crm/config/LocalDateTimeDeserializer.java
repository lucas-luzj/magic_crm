package com.magic.crm.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * LocalDateTime 反序列化器
 * 支持多种日期时间格式的自动解析
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    // 支持的日期时间格式
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ISO_DATE
    };

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        
        if (node.isNull()) {
            return null;
        }
        
        String dateTimeString = node.asText().trim();
        
        if (dateTimeString.isEmpty()) {
            return null;
        }
        
        // 尝试解析各种日期时间格式
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                // 如果只有日期没有时间，添加默认时间
                if (!dateTimeString.contains(" ")) {
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString + " 00:00:00", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return dateTime;
                } else {
                    return LocalDateTime.parse(dateTimeString, formatter);
                }
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
                continue;
            }
        }
        
        // 如果所有格式都失败，抛出异常
        throw new IOException("无法解析日期时间字符串: " + dateTimeString + 
            "。支持的格式包括: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd, yyyy/MM/dd 等");
    }
}
