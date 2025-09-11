package com.magic.crm.repository;

import com.magic.crm.entity.FormTemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 表单模板版本数据访问层
 */
@Repository
public interface FormTemplateVersionRepository extends JpaRepository<FormTemplateVersion, Long> {

    /**
     * 根据表单模板ID查找所有版本
     */
    List<FormTemplateVersion> findByFormTemplateIdOrderByVersionNumberDesc(Long formTemplateId);

    /**
     * 根据表单模板ID和版本号查找版本
     */
    Optional<FormTemplateVersion> findByFormTemplateIdAndVersionNumber(Long formTemplateId, Integer versionNumber);

    /**
     * 根据表单模板ID查找最新版本
     */
    @Query("SELECT ftv FROM FormTemplateVersion ftv WHERE ftv.formTemplateId = :formTemplateId " +
            "ORDER BY ftv.versionNumber DESC LIMIT 1")
    Optional<FormTemplateVersion> findLatestVersionByFormTemplateId(@Param("formTemplateId") Long formTemplateId);

    /**
     * 根据表单模板ID查找最大版本号
     */
    @Query("SELECT MAX(ftv.versionNumber) FROM FormTemplateVersion ftv WHERE ftv.formTemplateId = :formTemplateId")
    Integer findMaxVersionNumberByFormTemplateId(@Param("formTemplateId") Long formTemplateId);

    /**
     * 根据创建者查找版本
     */
    List<FormTemplateVersion> findByCreatedBy(String createdBy);

    /**
     * 根据表单模板ID统计版本数量
     */
    @Query("SELECT COUNT(ftv) FROM FormTemplateVersion ftv WHERE ftv.formTemplateId = :formTemplateId")
    Long countByFormTemplateId(@Param("formTemplateId") Long formTemplateId);

    /**
     * 根据表单模板ID删除所有版本
     */
    void deleteByFormTemplateId(Long formTemplateId);
}
