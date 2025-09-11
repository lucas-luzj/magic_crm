package com.magic.crm.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 通用日期时间反序列化器
 * 支持多种日期时间格式的自动解析
 */
public class DateTimeDeserializer extends JsonDeserializer<LocalDate> {

    // 支持的日期时间格式
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
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
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        
        if (node.isNull()) {
            return null;
        }
        
        String dateString = node.asText().trim();
        
        if (dateString.isEmpty()) {
            return null;
        }
        
        // 尝试解析各种日期格式
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                // 如果是日期时间格式，只取日期部分
                if (dateString.contains(" ")) {
                    LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                    return dateTime.toLocalDate();
                } else {
                    return LocalDate.parse(dateString, formatter);
                }
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
                continue;
            }
        }
        
        // 特殊处理：如果包含时间部分，尝试提取日期部分
        if (dateString.contains(" ")) {
            String datePart = dateString.split(" ")[0];
            try {
                return LocalDate.parse(datePart, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                // 忽略错误，继续下面的处理
            }
        }
        
        // 如果所有格式都失败，抛出异常
        throw new IOException("无法解析日期字符串: " + dateString + 
            "。支持的格式包括: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd, yyyy/MM/dd 等");
    }
}
