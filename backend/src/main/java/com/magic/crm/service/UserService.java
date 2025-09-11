package com.magic.crm.service;

import com.magic.crm.entity.User;
import com.magic.crm.repository.UserRepository;
import com.magic.crm.service.UserSyncService.UserCreatedEvent;
import com.magic.crm.service.UserSyncService.UserUpdatedEvent;
import com.magic.crm.service.UserSyncService.UserDeletedEvent;
import com.magic.crm.util.PinyinUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("用户不存在:" + username));
    }

    public User createUser(User request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(User.Role.USER);
        user.setIsActive(true);
        user.setDepartmentId(request.getDepartmentId());

        // 自动生成拼音首字母
        if (StringUtils.hasText(request.getFullName())) {
            user.setNamePinyin(PinyinUtil.getAccurateFirstLetters(request.getFullName()));
        }

        User savedUser = userRepository.save(user);

        // 发布用户创建事件
        eventPublisher.publishEvent(new UserCreatedEvent(savedUser));

        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateLastLoginTime(Long userId) {
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
    }

    public void updateLastLoginInfo(Long userId, String clientIp, String userAgent) {
        userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), clientIp, userAgent);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkPassword(Long id, String password) {
        var u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return passwordEncoder.matches(password, u.getPassword());
    }

    public void changePassword(Long id, String newPassword) {
        var u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
    }

    /**
     * 分页查询用户列表
     */
    public Page<User> findUsers(String keyword, Integer departmentId, int pageIndex, int pageSize) {
        // 创建分页对象，按创建时间倒序
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return userRepository.findByKeyword(keyword, departmentId, pageable);
    }

    /**
     * 保存或更新用户
     */
    public User saveOrUpdateUser(User user) {
        if (user.getId() == null || user.getId() == 0) {
            // 新增用户
            return createNewUser(user);
        } else {
            // 更新用户
            return updateExistingUser(user);
        }
    }

    /**
     * 创建新用户
     */
    private User createNewUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }

        // 加密密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            throw new RuntimeException("密码不能为空");
        }

        // 设置默认值
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        // 自动生成拼音首字母
        if (StringUtils.hasText(user.getFullName())) {
            user.setNamePinyin(PinyinUtil.getAccurateFirstLetters(user.getFullName()));
        }

        User savedUser = userRepository.save(user);

        // 发布用户创建事件
        eventPublisher.publishEvent(new UserCreatedEvent(savedUser));

        return savedUser;
    }

    /**
     * 更新现有用户
     */
    private User updateExistingUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + user.getId()));

        // 检查用户名是否被其他用户占用
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }

        // 检查邮箱是否被其他用户占用
        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }

        // 更新基础字段（必填字段直接更新）
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // 更新可选字段（null不更新，空字符串清空，有值更新）
        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }
        if (user.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getIsActive() != null) {
            existingUser.setIsActive(user.getIsActive());
        }
        if (user.getDepartmentId() != null) {
            existingUser.setDepartmentId(user.getDepartmentId());
        }

        // 更新新增字段
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getPhoto() != null) {
            existingUser.setPhoto(user.getPhoto());
        }
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }
        if (user.getLastLoginIp() != null) {
            existingUser.setLastLoginIp(user.getLastLoginIp());
        }
        if (user.getLastLoginUserAgent() != null) {
            existingUser.setLastLoginUserAgent(user.getLastLoginUserAgent());
        }
        if (user.getPosition() != null) {
            existingUser.setPosition(user.getPosition());
        }
        if (user.getWechatUnionId() != null) {
            existingUser.setWechatUnionId(user.getWechatUnionId());
        }
        if (user.getWechatOpenId() != null) {
            existingUser.setWechatOpenId(user.getWechatOpenId());
        }
        if (user.getDingtalkId() != null) {
            existingUser.setDingtalkId(user.getDingtalkId());
        }
        if (user.getQqNumber() != null) {
            existingUser.setQqNumber(user.getQqNumber());
        }
        if (user.getHireDate() != null) {
            existingUser.setHireDate(user.getHireDate());
        }
        if (user.getResignationDate() != null) {
            existingUser.setResignationDate(user.getResignationDate());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getHometown() != null) {
            existingUser.setHometown(user.getHometown());
        }
        if (user.getGraduateSchool() != null) {
            existingUser.setGraduateSchool(user.getGraduateSchool());
        }
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.getEmergencyContact() != null) {
            existingUser.setEmergencyContact(user.getEmergencyContact());
        }
        if (user.getEmergencyContactPhone() != null) {
            existingUser.setEmergencyContactPhone(user.getEmergencyContactPhone());
        }
        if (user.getRemarks() != null) {
            existingUser.setRemarks(user.getRemarks());
        }

        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(user.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 自动生成拼音首字母
        if (StringUtils.hasText(user.getFullName())) {
            existingUser.setNamePinyin(PinyinUtil.getAccurateFirstLetters(user.getFullName()));
        }

        User updatedUser = userRepository.save(existingUser);

        // 发布用户更新事件
        eventPublisher.publishEvent(new UserUpdatedEvent(updatedUser));

        return updatedUser;
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在: " + id);
        }

        // 发布用户删除事件
        eventPublisher.publishEvent(new UserDeletedEvent(id));

        userRepository.deleteById(id);
    }
}