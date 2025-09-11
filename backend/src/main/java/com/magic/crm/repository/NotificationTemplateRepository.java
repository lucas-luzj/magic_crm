package com.magic.crm.repository;

import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationTemplate;
import com.magic.crm.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板Repository接口
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    
    /**
     * 根据模板代码查询模板
     */
    Optional<NotificationTemplate> findByCode(String code);
    
    /**
     * 根据模板代码和是否启用查询模板
     */
    Optional<NotificationTemplate> findByCodeAndIsEnabled(String code, Boolean isEnabled);
    
    /**
     * 根据类型查询模板
     */
    List<NotificationTemplate> findByTypeAndIsEnabled(NotificationType type, Boolean isEnabled);
    
    /**
     * 根据渠道查询模板
     */
    List<NotificationTemplate> findByChannelAndIsEnabled(NotificationChannel channel, Boolean isEnabled);
    
    /**
     * 根据类型和渠道查询模板
     */
    List<NotificationTemplate> findByTypeAndChannelAndIsEnabled(NotificationType type, NotificationChannel channel, Boolean isEnabled);
    
    /**
     * 分页查询模板
     */
    Page<NotificationTemplate> findByIsEnabledOrderByCreatedAtDesc(Boolean isEnabled, Pageable pageable);
    
    /**
     * 根据名称模糊查询
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE " +
           "(:name IS NULL OR t.name LIKE %:name%) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:channel IS NULL OR t.channel = :channel) AND " +
           "(:isEnabled IS NULL OR t.isEnabled = :isEnabled) " +
           "ORDER BY t.createdAt DESC")
    Page<NotificationTemplate> findByConditions(@Param("name") String name,
                                               @Param("type") NotificationType type,
                                               @Param("channel") NotificationChannel channel,
                                               @Param("isEnabled") Boolean isEnabled,
                                               Pageable pageable);
    
    /**
     * 检查模板代码是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 检查模板代码是否存在（排除指定ID）
     */
    boolean existsByCodeAndIdNot(String code, Long id);
    
    /**
     * 根据创建者查询模板
     */
    List<NotificationTemplate> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    /**
     * 查询系统模板
     */
    List<NotificationTemplate> findByIsSystemAndIsEnabledOrderByCreatedAtDesc(Boolean isSystem, Boolean isEnabled);
    
    /**
     * 根据类型统计模板数量
     */
    @Query("SELECT t.type, COUNT(t) FROM NotificationTemplate t WHERE t.isEnabled = :isEnabled GROUP BY t.type")
    List<Object[]> countByTypeAndIsEnabled(@Param("isEnabled") Boolean isEnabled);
    
    /**
     * 根据渠道统计模板数量
     */
    @Query("SELECT t.channel, COUNT(t) FROM NotificationTemplate t WHERE t.isEnabled = :isEnabled GROUP BY t.channel")
    List<Object[]> countByChannelAndIsEnabled(@Param("isEnabled") Boolean isEnabled);
}
