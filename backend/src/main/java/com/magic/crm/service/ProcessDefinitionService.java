package com.magic.crm.service;

import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.ProcessDefinition;
import com.magic.crm.exception.BusinessException;
import com.magic.crm.repository.ProcessDefinitionRepository;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 流程定义服务
 * 
 * @author Magic CRM Team
 */
@Service
@Transactional
public class ProcessDefinitionService {

    @Autowired
    private ProcessDefinitionRepository processDefinitionRepository;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 部署流程定义
     */
    public ProcessDefinition deployProcess(String processName, String category,
            MultipartFile bpmnFile, Long userId) {
        try {
            // 创建部署构建器
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                    .name(processName)
                    .category(category);

            // 添加BPMN文件
            deploymentBuilder.addInputStream(bpmnFile.getOriginalFilename(),
                    bpmnFile.getInputStream());

            // 执行部署
            Deployment deployment = deploymentBuilder.deploy();

            // 获取部署的流程定义
            org.flowable.engine.repository.ProcessDefinition flowableProcessDef = repositoryService
                    .createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            if (flowableProcessDef == null) {
                throw new BusinessException("流程定义部署失败");
            }

            // 保存到自定义表
            ProcessDefinition processDefinition = ProcessDefinition.builder()
                    .processDefinitionId(flowableProcessDef.getId())
                    .processKey(flowableProcessDef.getKey())
                    .processName(flowableProcessDef.getName())
                    .description(flowableProcessDef.getDescription())
                    .version(flowableProcessDef.getVersion())
                    .category(flowableProcessDef.getCategory())
                    .status("ACTIVE")
                    .deploymentId(deployment.getId())
                    .resourceName(flowableProcessDef.getResourceName())
                    .diagramResourceName(flowableProcessDef.getDiagramResourceName())
                    .hasStartFormKey(flowableProcessDef.hasStartFormKey())
                    .hasGraphicalNotation(flowableProcessDef.hasGraphicalNotation())
                    .createdBy(userId)
                    .build();

            return processDefinitionRepository.save(processDefinition);

        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("流程部署失败: " + e.getMessage());
        }
    }

    /**
     * 根据BPMN XML内容部署流程
     */
    public ProcessDefinition deployProcessFromXml(String processName, String category,
            String bpmnXml, String fileName, Long userId) {
        try {
            // 确保fileName不为null
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = processName + ".bpmn";
            }

            // 创建部署
            Deployment deployment = repositoryService.createDeployment()
                    .name(processName)
                    .category(category)
                    .addString(fileName, bpmnXml)
                    .deploy();

            // 获取部署的流程定义
            org.flowable.engine.repository.ProcessDefinition flowableProcessDef = repositoryService
                    .createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            if (flowableProcessDef == null) {
                throw new BusinessException("流程定义部署失败");
            }

            // 保存到自定义表
            ProcessDefinition processDefinition = ProcessDefinition.builder()
                    .processDefinitionId(flowableProcessDef.getId())
                    .processKey(flowableProcessDef.getKey())
                    .processName(flowableProcessDef.getName())
                    .description(flowableProcessDef.getDescription())
                    .version(flowableProcessDef.getVersion())
                    .category(flowableProcessDef.getCategory())
                    .status("ACTIVE")
                    .deploymentId(deployment.getId())
                    .resourceName(flowableProcessDef.getResourceName())
                    .diagramResourceName(flowableProcessDef.getDiagramResourceName())
                    .hasStartFormKey(flowableProcessDef.hasStartFormKey())
                    .hasGraphicalNotation(flowableProcessDef.hasGraphicalNotation())
                    .createdBy(userId)
                    .build();

            return processDefinitionRepository.save(processDefinition);

        } catch (Exception e) {
            throw new BusinessException("流程部署失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询流程定义
     */
    public PageResponse<ProcessDefinition> getProcessDefinitions(String processName,
            String category,
            String status,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ProcessDefinition> pageResult = processDefinitionRepository
                .findByConditions(processName, category, status, pageable);

        return new PageResponse<>(pageResult);
    }

    /**
     * 根据ID获取流程定义
     */
    public ProcessDefinition getProcessDefinitionById(Long id) {
        return processDefinitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("流程定义不存在"));
    }

    /**
     * 根据流程定义ID获取流程定义
     */
    public ProcessDefinition getProcessDefinitionByProcessDefId(String processDefinitionId) {
        return processDefinitionRepository.findByProcessDefinitionId(processDefinitionId)
                .orElseThrow(() -> new BusinessException("流程定义不存在"));
    }

    /**
     * 激活流程定义
     */
    public void activateProcessDefinition(String processDefinitionId) {
        // 激活Flowable中的流程定义
        repositoryService.activateProcessDefinitionById(processDefinitionId);

        // 更新自定义表状态
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefinitionId);
        processDefinition.setStatus("ACTIVE");
        processDefinitionRepository.save(processDefinition);
    }

    /**
     * 挂起流程定义
     */
    public void suspendProcessDefinition(String processDefinitionId) {
        // 挂起Flowable中的流程定义
        repositoryService.suspendProcessDefinitionById(processDefinitionId);

        // 更新自定义表状态
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefinitionId);
        processDefinition.setStatus("SUSPENDED");
        processDefinitionRepository.save(processDefinition);
    }

    /**
     * 删除流程定义
     */
    public void deleteProcessDefinition(String processDefinitionId, boolean cascade) {
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefinitionId);

        // 删除Flowable中的部署（会同时删除流程定义）
        repositoryService.deleteDeployment(processDefinition.getDeploymentId(), cascade);

        // 删除自定义表记录
        processDefinitionRepository.delete(processDefinition);
    }

    /**
     * 获取流程定义的BPMN XML
     */
    public String getProcessDefinitionBpmnXml(String processDefinitionId) {
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefinitionId);

        try {
            return new String(repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(),
                    processDefinition.getResourceName()).readAllBytes());
        } catch (IOException e) {
            throw new BusinessException("获取流程定义XML失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程定义的流程图
     */
    public byte[] getProcessDefinitionDiagram(String processDefinitionId) {
        try {
            return repositoryService.getProcessDiagram(processDefinitionId).readAllBytes();
        } catch (IOException e) {
            throw new BusinessException("获取流程图失败: " + e.getMessage());
        }
    }

    /**
     * 根据流程Key获取最新版本的流程定义
     */
    public ProcessDefinition getLatestProcessDefinitionByKey(String processKey) {
        List<ProcessDefinition> definitions = processDefinitionRepository
                .findLatestByProcessKey(processKey);

        if (definitions.isEmpty()) {
            throw new BusinessException("流程定义不存在: " + processKey);
        }

        return definitions.get(0);
    }

    /**
     * 获取所有流程定义
     */
    public List<ProcessDefinition> getAllProcessDefinitions() {
        return processDefinitionRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * 根据分类获取流程定义
     */
    public List<ProcessDefinition> getProcessDefinitionsByCategory(String category) {
        return processDefinitionRepository.findByCategory(category);
    }

    /**
     * 检查流程定义是否存在
     */
    public boolean existsProcessDefinition(String processDefinitionId) {
        return processDefinitionRepository.existsByProcessDefinitionId(processDefinitionId);
    }

    /**
     * 保存流程定义的表单配置
     */
    public void saveFormConfig(String processDefinitionId, String formConfig) {
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefinitionId);
        processDefinition.setFormConfig(formConfig);
        processDefinitionRepository.save(processDefinition);
    }

    /**
     * 获取流程定义的表单配置
     */
    public String getFormConfig(String processDefinitionId) {
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefinitionId);
        return processDefinition.getFormConfig();
    }

    /**
     * 根据流程定义ID更新表单配置
     */
    public ProcessDefinition updateFormConfig(Long id, String formConfig) {
        ProcessDefinition processDefinition = getProcessDefinitionById(id);
        processDefinition.setFormConfig(formConfig);
        return processDefinitionRepository.save(processDefinition);
    }
}