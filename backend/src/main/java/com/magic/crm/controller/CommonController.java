package com.magic.crm.controller;

import com.magic.crm.dto.DmDTO;
import com.magic.crm.dto.FileUploadResponse;
import com.magic.crm.entity.User;
import com.magic.crm.exception.BusinessException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Value("${file.upload.path:/uploads}")
    private String uploadBasePath;

    // 允许的文件类型
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp");

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "mp4", "avi", "mov", "wmv", "flv", "webm", "mkv");

    private static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList(
            "mp3", "wav", "flac", "aac", "ogg", "wma");

    private static final List<String> ALLOWED_ARCHIVE_TYPES = Arrays.asList(
            "zip", "rar", "7z", "tar", "gz", "bz2");

    @PostMapping("/uploadFile")
    public FileUploadResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("modelName") String modelName,
            @RequestParam(value = "thumbSize", defaultValue = "100") Integer thumbSize,
            Authentication authentication) {

        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取当前用户
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("文件名无效");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 验证文件类型
        if (!isAllowedFileType(fileExtension)) {
            throw new BusinessException("不支持的文件类型: " + fileExtension);
        }

        try {
            // 生成文件名
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String fileName = uuid + "." + fileExtension;

            // 创建目录路径
            String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String relativePath = String.format("%s/%s/%s", modelName, userId, yearMonth);
            Path uploadDir = Paths.get(uploadBasePath, relativePath);

            // 确保目录存在
            Files.createDirectories(uploadDir);

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // 构建文件URL
            String fileUrl = String.format("/uploads/%s/%s", relativePath, fileName);

            FileUploadResponse.FileUploadResponseBuilder responseBuilder = FileUploadResponse.builder()
                    .code(200)
                    .success(true)
                    .message("OK")
                    .url(fileUrl)
                    .name(originalFilename);

            // 如果是图片，生成缩略图
            if (ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
                try {
                    String thumbFileName = uuid + ".thumb.jpg";
                    Path thumbPath = uploadDir.resolve(thumbFileName);

                    if (generateThumbnail(filePath.toFile(), thumbPath.toFile(), thumbSize)) {
                        String thumbUrl = String.format("/uploads/%s/%s", relativePath, thumbFileName);
                        responseBuilder.thumbUrl(thumbUrl);
                    }
                } catch (Exception e) {
                    // 缩略图生成失败不影响主文件上传
                    System.err.println("缩略图生成失败: " + e.getMessage());
                }
            }

            return responseBuilder.build();

        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("系统错误: " + e.getMessage());
        }
    }

    /**
     * 验证文件类型是否允许
     */
    private boolean isAllowedFileType(String extension) {
        return ALLOWED_DOCUMENT_TYPES.contains(extension) ||
                ALLOWED_IMAGE_TYPES.contains(extension) ||
                ALLOWED_VIDEO_TYPES.contains(extension) ||
                ALLOWED_AUDIO_TYPES.contains(extension) ||
                ALLOWED_ARCHIVE_TYPES.contains(extension);
    }

    /**
     * 生成缩略图
     */
    private boolean generateThumbnail(File sourceFile, File thumbFile, int thumbSize) {
        try {
            BufferedImage originalImage = ImageIO.read(sourceFile);
            if (originalImage == null) {
                return false;
            }

            // 计算缩略图尺寸，保持宽高比
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            int thumbWidth, thumbHeight;
            if (originalWidth > originalHeight) {
                thumbWidth = thumbSize;
                thumbHeight = (int) ((double) originalHeight / originalWidth * thumbSize);
            } else {
                thumbHeight = thumbSize;
                thumbWidth = (int) ((double) originalWidth / originalHeight * thumbSize);
            }

            // 创建缩略图
            BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbImage.createGraphics();

            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(originalImage, 0, 0, thumbWidth, thumbHeight, null);
            g2d.dispose();

            // 保存为JPEG格式
            return ImageIO.write(thumbImage, "jpg", thumbFile);

        } catch (Exception e) {
            System.err.println("生成缩略图失败: " + e.getMessage());
            return false;
        }
    }

    @Value("${file.dm-path:/dm}")
    private String dmPath;

    @PostMapping("/getDM")
    public DmDTO getDM(String tbname) throws Exception {
        var file = new File(dmPath + tbname + ".js");
        if (!file.exists()) {
            throw new FileNotFoundException("找不到文件：dm/" + tbname + ".js");
        }
        return new DmDTO(file);

    }
}