package com.magic.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private int code;
    private boolean success;
    private String message;
    private String url;
    private String name;
    private String thumbUrl; // 缩略图URL，仅图片文件有值
}