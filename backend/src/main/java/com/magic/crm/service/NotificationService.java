package com.magic.crm.service;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationPriority;
import com.magic.crm.entity.NotificationStatus;
import com.magic.crm.entity.NotificationType;
import com.magic.crm.repository.NotificationRepository;
import com.magic.crm.repository.NotificationSettingsRepository;
import com.magic.crm.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 通知服务类
 */
@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    // 暂时注释掉未使用的依赖
    // @Autowired
    // private NotificationSettingsRepository settingsRepository;
    
    /**
     * 发送通知
     */
    public Notification sendNotification(Long userId, String title, String content, 
                                       NotificationType type, NotificationChannel channel) {
        return sendNotification(userId, null, title, content, type, channel, 
                               NotificationPriority.NORMAL, null, null, null);
    }
    
    /**
     * 发送通知（完整参数）
     */
    public Notification sendNotification(Long userId, Long senderId, String title, String content,
                                       NotificationType type, NotificationChannel channel,
                                       NotificationPriority priority, String businessId, 
                                       String businessType, String actionUrl) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(senderId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setChannel(channel);
        notification.setPriority(priority);
        notification.setBusinessId(businessId);
        notification.setBusinessType(businessType);
        notification.setActionUrl(actionUrl);
        notification.setStatus(NotificationStatus.UNREAD);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * 批量发送通知
     */
    public List<Notification> sendBatchNotifications(List<Long> userIds, String title, String content,
                                                    NotificationType type, NotificationChannel channel) {
        return userIds.stream()
                .map(userId -> sendNotification(userId, title, content, type, channel))
                .toList();
    }
    
    /**
     * 获取用户通知列表
     */
    public PageResponse<Notification> getUserNotifications(Long userId, int page, int size,
                                                          NotificationType type, NotificationStatus status,
                                                          String keyword, LocalDateTime startTime, LocalDateTime endTime) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            notificationPage = notificationRepository.findByUserIdAndKeyword(userId, keyword, pageable);
        } else if (startTime != null && endTime != null) {
            notificationPage = notificationRepository.findByUserIdAndTimeRange(userId, startTime, endTime, pageable);
        } else if (type != null && status != null) {
            // 需要分别查询类型和状态
            notificationPage = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
            // 这里可以进一步过滤状态，或者创建一个新的查询方法
        } else if (type != null) {
            notificationPage = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else if (status != null) {
            notificationPage = notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        } else {
            notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        return new PageResponse<>(notificationPage);
    }
    
    /**
     * 获取未读通知数量
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }
    
    /**
     * 获取未读通知列表
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);
    }
    
    /**
     * 标记通知为已读
     */
    public boolean markAsRead(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (notification.getUserId().equals(userId)) {
                notification.markAsRead();
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 批量标记通知为已读
     */
    public int batchMarkAsRead(List<Long> notificationIds, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.markAsReadByIds(notificationIds, NotificationStatus.READ, now, userId);
    }
    
    /**
     * 标记所有通知为已读
     */
    public int markAllAsRead(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.markAllAsReadByUserId(userId, NotificationStatus.READ, 
                                                          NotificationStatus.UNREAD, now);
    }
    
    /**
     * 删除通知
     */
    public boolean deleteNotification(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (notification.getUserId().equals(userId)) {
                notification.setStatus(NotificationStatus.DELETED);
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 批量删除通知
     */
    public int batchDeleteNotifications(List<Long> notificationIds, Long userId) {
        return notificationRepository.deleteByIds(notificationIds, NotificationStatus.DELETED, userId);
    }
    
    /**
     * 获取通知详情
     */
    public Optional<Notification> getNotificationById(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (notification.getUserId().equals(userId)) {
                return Optional.of(notification);
            }
        }
        return Optional.empty();
    }
    
    /**
     * 清理过期通知
     */
    public int cleanExpiredNotifications() {
        return notificationRepository.deleteExpiredNotifications(LocalDateTime.now(), NotificationStatus.UNREAD);
    }
    
    /**
     * 获取需要发送的通知
     */
    public List<Notification> getNotificationsToSend(int maxRetryCount) {
        return notificationRepository.findNotificationsToSend(LocalDateTime.now(), maxRetryCount);
    }
    
    /**
     * 更新通知发送状态
     */
    public void updateNotificationSendStatus(Long notificationId, boolean isSent, String sendResult) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsSent(isSent);
            notification.setSendResult(sendResult);
            if (isSent) {
                notification.setSentTime(LocalDateTime.now());
            } else {
                notification.setRetryCount(notification.getRetryCount() + 1);
            }
            notificationRepository.save(notification);
        }
    }
    
    /**
     * 根据业务ID获取通知
     */
    public List<Notification> getNotificationsByBusiness(String businessId, String businessType) {
        return notificationRepository.findByBusinessIdAndBusinessType(businessId, businessType);
    }
    
    /**
     * 获取用户通知统计
     */
    public List<Object[]> getNotificationStats(Long userId, NotificationStatus status) {
        return notificationRepository.countByUserIdAndStatusGroupByType(userId, status);
    }
}
