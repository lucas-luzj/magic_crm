package com.magic.crm.service;

import com.magic.crm.entity.Department;
import com.magic.crm.entity.User;
import com.magic.crm.repository.DepartmentRepository;
import com.magic.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.idm.api.Group;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Flowable权限集成服务
 * 负责将CRM系统的用户和部门信息同步到Flowable身份服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowablePermissionService {

    private final IdentityService identityService;
    private final TaskService taskService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * 同步用户到Flowable身份服务
     */
    @Transactional
    public void syncUserToFlowable(User user) {
        try {
            // 检查用户是否已存在
            org.flowable.idm.api.User existingUser = identityService.createUserQuery()
                    .userId(user.getId().toString())
                    .singleResult();

            if (existingUser == null) {
                // 创建新用户
                org.flowable.idm.api.User flowableUser = identityService.newUser(user.getId().toString());
                flowableUser.setFirstName(user.getFullName());
                flowableUser.setLastName("");
                flowableUser.setEmail(user.getEmail());
                flowableUser.setPassword(user.getPassword());
                identityService.saveUser(flowableUser);
                log.info("Created Flowable user: {}", user.getUsername());
            } else {
                // 更新现有用户
                existingUser.setFirstName(user.getFullName());
                existingUser.setEmail(user.getEmail());
                identityService.saveUser(existingUser);
                log.info("Updated Flowable user: {}", user.getUsername());
            }

            // 同步用户角色组
            syncUserRoleGroups(user);

            // 同步用户部门组
            syncUserDepartmentGroups(user);

        } catch (Exception e) {
            log.error("Failed to sync user to Flowable: {}", user.getUsername(), e);
            throw new RuntimeException("同步用户到Flowable失败", e);
        }
    }

    /**
     * 同步用户角色组
     */
    private void syncUserRoleGroups(User user) {
        String userId = user.getId().toString();
        String roleGroupId = "ROLE_" + user.getRole().name();

        // 移除用户的所有角色组
        List<Group> userGroups = identityService.createGroupQuery()
                .groupMember(userId)
                .groupType("role")
                .list();

        for (Group group : userGroups) {
            identityService.deleteMembership(userId, group.getId());
        }

        // 确保角色组存在
        ensureRoleGroupExists(user.getRole().name());

        // 添加用户到新角色组
        identityService.createMembership(userId, roleGroupId);
        log.debug("Added user {} to role group {}", user.getUsername(), roleGroupId);
    }

    /**
     * 同步用户部门组
     */
    private void syncUserDepartmentGroups(User user) {
        String userId = user.getId().toString();

        // 移除用户的所有部门组
        List<Group> departmentGroups = identityService.createGroupQuery()
                .groupMember(userId)
                .groupType("department")
                .list();

        for (Group group : departmentGroups) {
            identityService.deleteMembership(userId, group.getId());
        }

        // 如果用户有部门，添加到部门组
        if (user.getDepartmentId() != null) {
            Optional<Department> department = departmentRepository.findById(user.getDepartmentId());
            if (department.isPresent()) {
                String departmentGroupId = "DEPT_" + user.getDepartmentId();

                // 确保部门组存在
                ensureDepartmentGroupExists(department.get());

                // 添加用户到部门组
                identityService.createMembership(userId, departmentGroupId);
                log.debug("Added user {} to department group {}", user.getUsername(), departmentGroupId);
            }
        }
    }

    /**
     * 确保角色组存在
     */
    private void ensureRoleGroupExists(String roleName) {
        String groupId = "ROLE_" + roleName;
        Group existingGroup = identityService.createGroupQuery()
                .groupId(groupId)
                .singleResult();

        if (existingGroup == null) {
            Group roleGroup = identityService.newGroup(groupId);
            roleGroup.setName(roleName + "角色");
            roleGroup.setType("role");
            identityService.saveGroup(roleGroup);
            log.info("Created role group: {}", groupId);
        }
    }

    /**
     * 确保部门组存在
     */
    private void ensureDepartmentGroupExists(Department department) {
        String groupId = "DEPT_" + department.getId();
        Group existingGroup = identityService.createGroupQuery()
                .groupId(groupId)
                .singleResult();

        if (existingGroup == null) {
            Group departmentGroup = identityService.newGroup(groupId);
            departmentGroup.setName(department.getName());
            departmentGroup.setType("department");
            identityService.saveGroup(departmentGroup);
            log.info("Created department group: {}", groupId);
        }
    }

    /**
     * 删除Flowable用户
     */
    @Transactional
    public void deleteFlowableUser(Long userId) {
        try {
            String userIdStr = userId.toString();
            org.flowable.idm.api.User flowableUser = identityService.createUserQuery()
                    .userId(userIdStr)
                    .singleResult();

            if (flowableUser != null) {
                identityService.deleteUser(userIdStr);
                log.info("Deleted Flowable user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to delete Flowable user: {}", userId, e);
            throw new RuntimeException("删除Flowable用户失败", e);
        }
    }

    /**
     * 同步所有用户到Flowable
     */
    @Transactional
    public void syncAllUsersToFlowable() {
        log.info("Starting to sync all users to Flowable...");

        List<User> users = userRepository.findAll();
        int successCount = 0;
        int failCount = 0;

        for (User user : users) {
            try {
                if (user.getIsActive()) {
                    syncUserToFlowable(user);
                    successCount++;
                } else {
                    deleteFlowableUser(user.getId());
                }
            } catch (Exception e) {
                log.error("Failed to sync user: {}", user.getUsername(), e);
                failCount++;
            }
        }

        log.info("Sync completed. Success: {}, Failed: {}", successCount, failCount);
    }

    /**
     * 检查用户是否有流程权限
     */
    public boolean hasProcessPermission(Long userId, String processDefinitionKey, String permission) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.getIsActive()) {
            return false;
        }

        // 管理员拥有所有权限
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }

        // 根据角色和权限类型判断
        switch (permission) {
            case "READ":
                // 所有用户都可以查看流程
                return true;
            case "START":
                // 用户和管理员可以启动流程
                return user.getRole() == User.Role.USER || user.getRole() == User.Role.MANAGER;
            case "MANAGE":
                // 只有管理员和经理可以管理流程
                return user.getRole() == User.Role.MANAGER || user.getRole() == User.Role.ADMIN;
            default:
                return false;
        }
    }

    /**
     * 检查用户是否有任务权限
     */
    public boolean hasTaskPermission(Long userId, String taskId, String permission) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.getIsActive()) {
            return false;
        }

        // 管理员拥有所有权限
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }

        // 获取任务信息
        org.flowable.task.api.Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return false;
        }

        // 检查是否是任务的分配人
        if (task.getAssignee() != null && task.getAssignee().equals(userId.toString())) {
            return true;
        }

        // 检查是否是候选用户
        List<org.flowable.task.api.Task> candidateTasks = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(userId.toString())
                .list();

        if (!candidateTasks.isEmpty()) {
            return true;
        }

        // 检查是否是候选组成员
        List<Group> userGroups = identityService.createGroupQuery()
                .groupMember(userId.toString())
                .list();

        for (Group group : userGroups) {
            List<org.flowable.task.api.Task> groupTasks = taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskCandidateGroup(group.getId())
                    .list();

            if (!groupTasks.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}