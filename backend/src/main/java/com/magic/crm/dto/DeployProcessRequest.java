package com.magic.crm.dto;

import lombok.Data;

/**
 * 部署流程定义请求DTO
 */
@Data
public class DeployProcessRequest {
    
    /**
     * 流程名称
     */
    private String processName;
    
    /**
     * 流程分类
     */
    private String category;
    
    /**
     * BPMN XML内容
     */
    private String bpmnXml;
    
    /**
     * 文件名
     */
    private String fileName;
}