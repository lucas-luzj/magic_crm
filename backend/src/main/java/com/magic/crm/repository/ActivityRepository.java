package com.magic.crm.repository;

import com.magic.crm.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 跟进记录数据访问层
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID>, JpaSpecificationExecutor<Activity> {
    
    /**
     * 根据客户ID查找跟进记录
     */
    Page<Activity> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * 根据联系人ID查找跟进记录
     */
    Page<Activity> findByContactId(UUID contactId, Pageable pageable);
    
    /**
     * 根据项目ID查找跟进记录
     */
    Page<Activity> findByProjectId(UUID projectId, Pageable pageable);
    
    /**
     * 根据商机ID查找跟进记录
     */
    Page<Activity> findByOpportunityId(UUID opportunityId, Pageable pageable);
    
    /**
     * 根据活动类型查找
     */
    List<Activity> findByActivityType(String activityType);
    
    /**
     * 根据创建人查找
     */
    Page<Activity> findByCreatedBy(UUID createdBy, Pageable pageable);
    
    /**
     * 查找时间范围内的跟进记录
     */
    @Query("SELECT a FROM Activity a WHERE a.startTime >= :startTime AND a.endTime <= :endTime " +
           "AND a.deleted = false ORDER BY a.startTime")
    List<Activity> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找某用户时间范围内的跟进记录
     */
    @Query("SELECT a FROM Activity a WHERE a.createdBy = :userId " +
           "AND a.startTime >= :startTime AND a.endTime <= :endTime " +
           "AND a.deleted = false ORDER BY a.startTime")
    List<Activity> findByUserAndTimeRange(@Param("userId") UUID userId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找需要跟进的记录
     */
    @Query("SELECT a FROM Activity a WHERE a.nextFollowTime <= :now " +
           "AND a.status != 'COMPLETED' AND a.deleted = false")
    List<Activity> findPendingFollowUps(@Param("now") LocalDateTime now);
    
    /**
     * 查找某用户需要跟进的记录
     */
    @Query("SELECT a FROM Activity a WHERE a.createdBy = :userId " +
           "AND a.nextFollowTime <= :now AND a.status != 'COMPLETED' " +
           "AND a.deleted = false")
    List<Activity> findUserPendingFollowUps(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    /**
     * 获取客户最后一次跟进记录
     */
    @Query("SELECT a FROM Activity a WHERE a.customerId = :customerId " +
           "AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<Activity> findLastActivityByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
    
    /**
     * 统计客户的跟进次数
     */
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.customerId = :customerId AND a.deleted = false")
    Long countByCustomerId(@Param("customerId") UUID customerId);
    
    /**
     * 统计用户在时间范围内的跟进次数
     */
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.createdBy = :userId " +
           "AND a.createdAt >= :startTime AND a.createdAt <= :endTime " +
           "AND a.deleted = false")
    Long countUserActivitiesInRange(@Param("userId") UUID userId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按活动类型统计
     */
    @Query("SELECT a.activityType, COUNT(a) FROM Activity a " +
           "WHERE a.createdAt >= :startTime AND a.createdAt <= :endTime " +
           "AND a.deleted = false GROUP BY a.activityType")
    List<Object[]> countByActivityTypeInRange(@Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 搜索跟进记录
     */
    @Query("SELECT a FROM Activity a WHERE " +
           "(LOWER(a.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND a.deleted = false")
    Page<Activity> searchActivities(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 软删除跟进记录
     */
    @Modifying
    @Query("UPDATE Activity a SET a.deleted = true, a.updatedAt = :now WHERE a.id = :activityId")
    void softDelete(@Param("activityId") UUID activityId, @Param("now") LocalDateTime now);
    
    /**
     * 批量软删除跟进记录
     */
    @Modifying
    @Query("UPDATE Activity a SET a.deleted = true, a.updatedAt = :now WHERE a.id IN :activityIds")
    void softDeleteBatch(@Param("activityIds") List<UUID> activityIds, @Param("now") LocalDateTime now);
    
    /**
     * 根据客户ID批量软删除跟进记录
     */
    @Modifying
    @Query("UPDATE Activity a SET a.deleted = true, a.updatedAt = :now WHERE a.customerId = :customerId")
    void softDeleteByCustomerId(@Param("customerId") UUID customerId, @Param("now") LocalDateTime now);
}