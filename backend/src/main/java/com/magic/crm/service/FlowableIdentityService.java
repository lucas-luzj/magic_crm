package com.magic.crm.service;

import com.magic.crm.entity.User;
import com.magic.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Flowable身份服务集成
 * 将现有用户系统与Flowable身份管理集成
 * 
 * @author Magic CRM Team
 */
@Service
public class FlowableIdentityService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户ID获取用户信息
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * 根据用户名获取用户信息
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * 获取所有用户列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 检查用户是否存在
     */
    public boolean userExists(String userId) {
        try {
            Long id = Long.parseLong(userId);
            return userRepository.existsById(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证用户密码
     */
    public boolean checkPassword(String userId, String password) {
        try {
            Long id = Long.parseLong(userId);
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return false;
            }
            // 这里应该使用密码加密验证，简化处理
            return user.getPassword().equals(password);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}