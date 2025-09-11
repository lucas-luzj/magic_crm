package com.magic.crm.repository;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationStatus;
import com.magic.crm.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知Repository接口
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 根据用户ID分页查询通知
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态分页查询通知
     */
    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status, Pageable pageable);
    
    /**
     * 根据用户ID和类型分页查询通知
     */
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type, Pageable pageable);
    
    /**
     * 根据用户ID和渠道分页查询通知
     */
    Page<Notification> findByUserIdAndChannelOrderByCreatedAtDesc(Long userId, NotificationChannel channel, Pageable pageable);
    
    /**
     * 根据用户ID查询未读通知数量
     */
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
    
    /**
     * 根据用户ID查询未读通知
     */
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);
    
    /**
     * 根据用户ID和业务ID查询通知
     */
    List<Notification> findByUserIdAndBusinessIdAndBusinessType(Long userId, String businessId, String businessType);
    
    /**
     * 根据用户ID和时间范围查询通知
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND n.createdAt BETWEEN :startTime AND :endTime " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndTimeRange(@Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               Pageable pageable);
    
    /**
     * 根据用户ID和关键词搜索通知
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndKeyword(@Param("userId") Long userId,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);
    
    /**
     * 批量标记通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.readTime = :readTime " +
           "WHERE n.id IN :ids AND n.userId = :userId")
    int markAsReadByIds(@Param("ids") List<Long> ids,
                       @Param("status") NotificationStatus status,
                       @Param("readTime") LocalDateTime readTime,
                       @Param("userId") Long userId);
    
    /**
     * 标记用户所有通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.readTime = :readTime " +
           "WHERE n.userId = :userId AND n.status = :oldStatus")
    int markAllAsReadByUserId(@Param("userId") Long userId,
                             @Param("status") NotificationStatus status,
                             @Param("oldStatus") NotificationStatus oldStatus,
                             @Param("readTime") LocalDateTime readTime);
    
    /**
     * 删除过期的通知
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expireTime < :now AND n.status = :status")
    int deleteExpiredNotifications(@Param("now") LocalDateTime now,
                                  @Param("status") NotificationStatus status);
    
    /**
     * 根据用户ID删除通知
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = :status WHERE n.id IN :ids AND n.userId = :userId")
    int deleteByIds(@Param("ids") List<Long> ids,
                   @Param("status") NotificationStatus status,
                   @Param("userId") Long userId);
    
    /**
     * 查询需要发送的通知
     */
    @Query("SELECT n FROM Notification n WHERE n.isSent = false " +
           "AND (n.expireTime IS NULL OR n.expireTime > :now) " +
           "AND n.retryCount < :maxRetryCount " +
           "ORDER BY n.priority DESC, n.createdAt ASC")
    List<Notification> findNotificationsToSend(@Param("now") LocalDateTime now,
                                              @Param("maxRetryCount") Integer maxRetryCount);
    
    /**
     * 根据业务ID和业务类型查询通知
     */
    List<Notification> findByBusinessIdAndBusinessType(String businessId, String businessType);
    
    /**
     * 统计用户各类型通知数量
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.userId = :userId " +
           "AND n.status = :status GROUP BY n.type")
    List<Object[]> countByUserIdAndStatusGroupByType(@Param("userId") Long userId,
                                                    @Param("status") NotificationStatus status);
}
