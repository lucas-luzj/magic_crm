package com.denwon.crm.module.system.service.impl;

import com.denwon.crm.common.exception.BusinessException;
import com.denwon.crm.module.system.dto.UserCreateDTO;
import com.denwon.crm.module.system.dto.UserUpdateDTO;
import com.denwon.crm.module.system.entity.Role;
import com.denwon.crm.module.system.entity.User;
import com.denwon.crm.module.system.repository.RoleRepository;
import com.denwon.crm.module.system.repository.UserRepository;
import com.denwon.crm.module.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户服务实现
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }
    
    @Override
    @Transactional
    public User createUser(UserCreateDTO dto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }
        
        // 检查手机号是否已存在
        if (dto.getMobile() != null && userRepository.existsByMobile(dto.getMobile())) {
            throw new BusinessException("手机号已被使用");
        }
        
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setStatus(1);
        
        // 设置默认角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateDTO dto) {
        User user = getUserById(id);
        
        // 检查邮箱唯一性
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(dto.getEmail());
        }
        
        // 检查手机号唯一性
        if (dto.getMobile() != null && !dto.getMobile().equals(user.getMobile())) {
            if (userRepository.existsByMobile(dto.getMobile())) {
                throw new BusinessException("手机号已被使用");
            }
            user.setMobile(dto.getMobile());
        }
        
        // 更新其他字段
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getTitle() != null) user.setTitle(dto.getTitle());
        if (dto.getEmployeeNo() != null) user.setEmployeeNo(dto.getEmployeeNo());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(0);
        userRepository.save(user);
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("用户不存在"));
    }
    
    @Override
    public Page<User> getUsers(Map<String, Object> filters, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 排除已删除
            predicates.add(cb.equal(root.get("deleted"), false));
            
            // 按条件过滤
            if (filters != null) {
                filters.forEach((key, value) -> {
                    if (value != null) {
                        switch (key) {
                            case "username":
                                predicates.add(cb.like(root.get("username"), "%" + value + "%"));
                                break;
                            case "name":
                                predicates.add(cb.like(root.get("name"), "%" + value + "%"));
                                break;
                            case "mobile":
                                predicates.add(cb.like(root.get("mobile"), "%" + value + "%"));
                                break;
                            case "email":
                                predicates.add(cb.like(root.get("email"), "%" + value + "%"));
                                break;
                            case "status":
                                predicates.add(cb.equal(root.get("status"), value));
                                break;
                            case "orgUnitId":
                                predicates.add(cb.equal(root.get("orgUnit").get("id"), value));
                                break;
                        }
                    }
                });
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return userRepository.findAll(spec, pageable);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = getUserById(userId);
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void lockUser(Long userId, Integer minutes) {
        User user = getUserById(userId);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(minutes));
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void unlockUser(Long userId) {
        User user = getUserById(userId);
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void updateLoginInfo(Long userId, String ip) {
        User user = getUserById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void recordLoginFailure(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            }
            
            userRepository.save(user);
        });
    }
}