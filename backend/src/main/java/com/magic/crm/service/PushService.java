package com.magic.crm.service;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import com.magic.crm.entity.Notification;
import com.magic.crm.entity.User;
import com.magic.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 推送服务
 */
@Service
public class PushService {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发送推送消息
     */
    public boolean sendPushMessage(String userId, String title, String content) {
        try {
            Map<String, String> config = systemSettingService.getPushConfig();

            if (!StringUtils.hasText(userId) || !StringUtils.hasText(title) || !StringUtils.hasText(content)) {
                System.err.println("用户ID、标题或内容为空");
                return false;
            }

            // 创建JPush客户端
            JPushClient jpushClient = new JPushClient(
                    config.get("masterSecret"),
                    config.get("appKey"));

            // 构建推送消息
            PushPayload payload = buildPushPayload(userId, title, content);

            // 发送推送
            PushResult result = jpushClient.sendPush(payload);

            if (result.isResultOK()) {
                System.out.println("推送消息发送成功: " + userId);
                return true;
            } else {
                System.err.println("推送消息发送失败: " + result);
                return false;
            }
        } catch (Exception e) {
            System.err.println("发送推送消息异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送通知推送
     */
    public boolean sendNotificationPush(Notification notification) {
        try {
            User user = userRepository.findById(notification.getUserId()).orElse(null);
            if (user == null) {
                System.err.println("用户不存在: " + notification.getUserId());
                return false;
            }

            // 这里简化处理，实际应该根据用户的设备信息发送
            // 假设userId就是推送的别名
            return sendPushMessage(String.valueOf(notification.getUserId()),
                    notification.getTitle(),
                    notification.getContent());
        } catch (Exception e) {
            System.err.println("发送通知推送失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 构建推送载荷
     */
    private PushPayload buildPushPayload(String userId, String title, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(userId))
                .setNotification(cn.jpush.api.push.model.notification.Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title)
                                .setAlert(content)
                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(content)
                                .setBadge(1)
                                .setSound("default")
                                .build())
                        .build())
                .setMessage(Message.content(content))
                .setOptions(Options.newBuilder()
                        .setApnsProduction(Boolean
                                .parseBoolean(systemSettingService.getSettingValue("push.jpush.isProduction", "false")))
                        .build())
                .build();
    }

    /**
     * 发送广播推送
     */
    public boolean sendBroadcastPush(String title, String content) {
        try {
            Map<String, String> config = systemSettingService.getPushConfig();

            JPushClient jpushClient = new JPushClient(
                    config.get("masterSecret"),
                    config.get("appKey"));

            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.all())
                    .setNotification(cn.jpush.api.push.model.notification.Notification.newBuilder()
                            .setAlert(content)
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .setTitle(title)
                                    .setAlert(content)
                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .setAlert(content)
                                    .setBadge(1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .setMessage(Message.content(content))
                    .setOptions(Options.newBuilder()
                            .setApnsProduction(Boolean.parseBoolean(config.get("isProduction")))
                            .build())
                    .build();

            PushResult result = jpushClient.sendPush(payload);

            if (result.isResultOK()) {
                System.out.println("广播推送发送成功");
                return true;
            } else {
                System.err.println("广播推送发送失败: " + result);
                return false;
            }
        } catch (Exception e) {
            System.err.println("发送广播推送异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 测试推送配置
     */
    public boolean testPushConfig(String userId, String title, String content) {
        try {
            return sendPushMessage(userId, title, content);
        } catch (Exception e) {
            System.err.println("测试推送配置失败: " + e.getMessage());
            return false;
        }
    }
}
