package com.magic.crm.dto;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.magic.crm.util.CustomBase64Encoder;

import ch.qos.logback.core.util.MD5Util;
import lombok.Data;

@Data
public class DmDTO {
    private String name;// tbname文件名
    private Long tick;// 文件最后修改时间
    private String Hash;// 文件的md5哈希值
    private String Code;// 文件文本编码

    public DmDTO(File file) throws Exception {
        this.tick = file.lastModified();
        this.name = file.getName();

        var text = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        this.Code = CustomBase64Encoder.encode(text);
        var md5 = new MD5Util();
        var hexHash = md5.md5Hash(text);
        this.Hash = md5.asHexString(hexHash);
    }
}
