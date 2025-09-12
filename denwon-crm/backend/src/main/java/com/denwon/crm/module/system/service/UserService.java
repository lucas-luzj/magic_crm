package com.denwon.crm.module.system.service;

import com.denwon.crm.common.response.PageResponse;
import com.denwon.crm.module.system.dto.UserDTO;
import com.denwon.crm.module.system.dto.UserCreateDTO;
import com.denwon.crm.module.system.dto.UserUpdateDTO;
import com.denwon.crm.module.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
public interface UserService extends UserDetailsService {
    
    /**
     * 创建用户
     */
    User createUser(UserCreateDTO dto);
    
    /**
     * 更新用户
     */
    User updateUser(Long id, UserUpdateDTO dto);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);
    
    /**
     * 分页查询用户
     */
    Page<User> getUsers(Map<String, Object> filters, Pageable pageable);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);
    
    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 锁定用户
     */
    void lockUser(Long userId, Integer minutes);
    
    /**
     * 解锁用户
     */
    void unlockUser(Long userId);
    
    /**
     * 更新登录信息
     */
    void updateLoginInfo(Long userId, String ip);
    
    /**
     * 记录登录失败
     */
    void recordLoginFailure(String username);
}