package com.magic.crm.service;

import com.magic.crm.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 用户同步服务
 * 监听用户变更事件，自动同步到Flowable身份服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final FlowablePermissionService flowablePermissionService;

    /**
     * 用户创建事件
     */
    public static class UserCreatedEvent {
        private final User user;

        public UserCreatedEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    /**
     * 用户更新事件
     */
    public static class UserUpdatedEvent {
        private final User user;

        public UserUpdatedEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    /**
     * 用户删除事件
     */
    public static class UserDeletedEvent {
        private final Long userId;

        public UserDeletedEvent(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }

    /**
     * 处理用户创建事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        try {
            log.info("处理用户创建事件: {}", event.getUser().getUsername());
            flowablePermissionService.syncUserToFlowable(event.getUser());
        } catch (Exception e) {
            log.error("处理用户创建事件失败: {}", event.getUser().getUsername(), e);
        }
    }

    /**
     * 处理用户更新事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdated(UserUpdatedEvent event) {
        try {
            log.info("处理用户更新事件: {}", event.getUser().getUsername());
            flowablePermissionService.syncUserToFlowable(event.getUser());
        } catch (Exception e) {
            log.error("处理用户更新事件失败: {}", event.getUser().getUsername(), e);
        }
    }

    /**
     * 处理用户删除事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {
        try {
            log.info("处理用户删除事件: {}", event.getUserId());
            flowablePermissionService.deleteFlowableUser(event.getUserId());
        } catch (Exception e) {
            log.error("处理用户删除事件失败: {}", event.getUserId(), e);
        }
    }
}