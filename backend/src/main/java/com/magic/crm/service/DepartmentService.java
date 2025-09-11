package com.magic.crm.service;

import com.magic.crm.dto.DepartmentDTO;
import com.magic.crm.entity.Department;
import com.magic.crm.entity.User;
import com.magic.crm.exception.BusinessException;
import com.magic.crm.repository.DepartmentRepository;
import com.magic.crm.repository.UserRepository;
import com.magic.crm.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 部门服务层
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    /**
     * 获取部门树形结构
     */
    public List<DepartmentDTO> getDepartmentTree() {
        List<Department> rootDepartments = departmentRepository.findByParentIdIsNullOrderBySortOrderAsc();
        var data = rootDepartments.stream()
                .map(this::buildDepartmentTree)
                .collect(Collectors.toList());

        return data;
    }

    /**
     * 构建部门树
     */
    private DepartmentDTO buildDepartmentTree(Department department) {
        DepartmentDTO dto = BeanCopyUtils.copyBean(department, DepartmentDTO.class);

        // 设置管理员姓名
        if (department.getManagerId() != null) {
            userRepository.findById(department.getManagerId())
                    .ifPresent(user -> dto.setManagerName(user.getFullName()));
        }

        // 设置用户数量
        dto.setUserCount(departmentRepository.countUsersByDepartmentId(department.getId()));

        // 递归构建子部门
        List<Department> children = departmentRepository.findByParentIdOrderBySortOrderAsc(department.getId());
        if (!children.isEmpty()) {
            dto.setChildren(children.stream()
                    .map(this::buildDepartmentTree)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * 根据ID获取部门详情
     */
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部门不存在"));

        DepartmentDTO dto = BeanCopyUtils.copyBean(department, DepartmentDTO.class);

        // 设置管理员姓名
        if (department.getManagerId() != null) {
            userRepository.findById(department.getManagerId())
                    .ifPresent(user -> dto.setManagerName(user.getFullName()));
        }

        // 设置父部门名称
        if (department.getParentId() != null) {
            departmentRepository.findById(department.getParentId())
                    .ifPresent(parent -> dto.setParentName(parent.getName()));
        }

        // 设置用户数量
        dto.setUserCount(departmentRepository.countUsersByDepartmentId(department.getId()));

        return dto;
    }

    /**
     * 创建部门
     */
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        // 验证部门编码唯一性
        if (StringUtils.hasText(departmentDTO.getCode())) {
            if (departmentRepository.findByCode(departmentDTO.getCode()).isPresent()) {
                throw new BusinessException("部门编码已存在");
            }
        }

        // 验证父部门存在性
        if (departmentDTO.getParentId() != null) {
            if (!departmentRepository.existsById(departmentDTO.getParentId())) {
                throw new BusinessException("父部门不存在");
            }
        }

        // 验证管理员存在性
        if (departmentDTO.getManagerId() != null) {
            if (!userRepository.existsById(departmentDTO.getManagerId())) {
                throw new BusinessException("指定的管理员不存在");
            }
        }

        Department department = BeanCopyUtils.copyBean(departmentDTO, Department.class);
        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }
        if (department.getIsActive() == null) {
            department.setIsActive(true);
        }

        Department savedDepartment = departmentRepository.save(department);
        return BeanCopyUtils.copyBean(savedDepartment, DepartmentDTO.class);
    }

    /**
     * 更新部门
     */
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部门不存在"));

        // 验证部门编码唯一性
        if (StringUtils.hasText(departmentDTO.getCode())) {
            if (departmentRepository.existsByCodeAndIdNot(departmentDTO.getCode(), id)) {
                throw new BusinessException("部门编码已存在");
            }
        }

        // 验证父部门存在性和循环引用
        if (departmentDTO.getParentId() != null) {
            if (!departmentRepository.existsById(departmentDTO.getParentId())) {
                throw new BusinessException("父部门不存在");
            }

            // 检查是否会形成循环引用
            if (isCircularReference(id, departmentDTO.getParentId())) {
                throw new BusinessException("不能将部门设置为自己或子部门的父部门");
            }
        }

        // 验证管理员存在性
        if (departmentDTO.getManagerId() != null) {
            if (!userRepository.existsById(departmentDTO.getManagerId())) {
                throw new BusinessException("指定的管理员不存在");
            }
        }

        // 更新部门信息
        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setParentId(departmentDTO.getParentId());
        existingDepartment.setManagerId(departmentDTO.getManagerId());
        existingDepartment.setDescription(departmentDTO.getDescription());
        existingDepartment.setSortOrder(departmentDTO.getSortOrder());
        existingDepartment.setIsActive(departmentDTO.getIsActive());

        Department savedDepartment = departmentRepository.save(existingDepartment);
        return BeanCopyUtils.copyBean(savedDepartment, DepartmentDTO.class);
    }

    /**
     * 删除部门
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部门不存在"));

        // 检查是否有子部门
        List<Department> children = departmentRepository.findByParentIdOrderBySortOrderAsc(id);
        if (!children.isEmpty()) {
            throw new BusinessException("该部门下还有子部门，无法删除");
        }

        // 检查是否有用户
        long userCount = departmentRepository.countUsersByDepartmentId(id);
        if (userCount > 0) {
            throw new BusinessException("该部门下还有用户，无法删除");
        }

        departmentRepository.delete(department);
    }

    /**
     * 获取所有启用的部门列表
     */
    public List<DepartmentDTO> getActiveDepartments() {
        List<Department> departments = departmentRepository.findByIsActiveTrueOrderBySortOrderAsc();
        return departments.stream()
                .map(dept -> BeanCopyUtils.copyBean(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 根据名称搜索部门
     */
    public List<DepartmentDTO> searchDepartmentsByName(String name) {
        List<Department> departments = departmentRepository.findByNameContainingIgnoreCaseOrderBySortOrderAsc(name);
        return departments.stream()
                .map(dept -> BeanCopyUtils.copyBean(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 检查循环引用
     */
    private boolean isCircularReference(Long departmentId, Long parentId) {
        if (departmentId.equals(parentId)) {
            return true;
        }

        Optional<Department> parent = departmentRepository.findById(parentId);
        if (parent.isPresent() && parent.get().getParentId() != null) {
            return isCircularReference(departmentId, parent.get().getParentId());
        }

        return false;
    }

    // ========== 为POST接口提供的方法 ==========

    /**
     * 分页查询部门列表
     * 支持关键词搜索（部门名称、编码）
     */
    public Page<Department> findDepartments(String keyword, Integer parentId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        return departmentRepository.findByKeyword(keyword, parentId, pageable);
    }

    /**
     * 根据ID获取部门实体
     */
    public Department findById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));

        // 设置关联信息
        if (department.getManagerId() != null) {
            userRepository.findById(department.getManagerId())
                    .ifPresent(user -> department.setManagerName(user.getFullName()));
        }

        if (department.getParentId() != null) {
            departmentRepository.findById(department.getParentId())
                    .ifPresent(parent -> department.setParentName(parent.getName()));
        }

        return department;
    }

    /**
     * 保存或更新部门
     * id为null或0时新增，否则更新
     */
    @Transactional
    public Department saveOrUpdateDepartment(Department department) {
        if (department.getId() == null || department.getId() == 0) {
            // 新增部门
            return createNewDepartment(department);
        } else {
            // 更新部门
            return updateExistingDepartment(department);
        }
    }

    /**
     * 创建新部门
     */
    private Department createNewDepartment(Department department) {
        // 验证部门编码唯一性
        if (StringUtils.hasText(department.getCode())) {
            if (departmentRepository.findByCode(department.getCode()).isPresent()) {
                throw new RuntimeException("部门编码已存在");
            }
        }

        // 验证父部门存在性
        if (department.getParentId() != null) {
            if (!departmentRepository.existsById(department.getParentId())) {
                throw new RuntimeException("父部门不存在");
            }
        }

        // 验证管理员存在性
        if (department.getManagerId() != null) {
            if (!userRepository.existsById(department.getManagerId())) {
                throw new RuntimeException("指定的管理员不存在");
            }
        }

        // 设置默认值
        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }
        if (department.getIsActive() == null) {
            department.setIsActive(true);
        }

        return departmentRepository.save(department);
    }

    /**
     * 更新现有部门
     */
    private Department updateExistingDepartment(Department department) {
        Department existingDepartment = departmentRepository.findById(department.getId())
                .orElseThrow(() -> new RuntimeException("部门不存在: " + department.getId()));

        // 验证部门编码唯一性
        if (StringUtils.hasText(department.getCode())) {
            if (departmentRepository.existsByCodeAndIdNot(department.getCode(), department.getId())) {
                throw new RuntimeException("部门编码已存在");
            }
        }

        // 验证父部门存在性和循环引用
        if (department.getParentId() != null) {
            if (!departmentRepository.existsById(department.getParentId())) {
                throw new RuntimeException("父部门不存在");
            }

            // 检查是否会形成循环引用
            if (isCircularReference(department.getId(), department.getParentId())) {
                throw new RuntimeException("不能将部门设置为自己或子部门的父部门");
            }
        }

        // 验证管理员存在性
        if (department.getManagerId() != null) {
            if (!userRepository.existsById(department.getManagerId())) {
                throw new RuntimeException("指定的管理员不存在");
            }
        }

        // 更新字段（null不更新，空字符串清空，有值更新）
        if (department.getName() != null) {
            existingDepartment.setName(department.getName());
        }
        if (department.getCode() != null) {
            existingDepartment.setCode(department.getCode());
        }
        if (department.getParentId() != null) {
            existingDepartment.setParentId(department.getParentId());
        }
        if (department.getManagerId() != null) {
            existingDepartment.setManagerId(department.getManagerId());
        }
        if (department.getDescription() != null) {
            existingDepartment.setDescription(department.getDescription());
        }
        if (department.getSortOrder() != null) {
            existingDepartment.setSortOrder(department.getSortOrder());
        }
        if (department.getIsActive() != null) {
            existingDepartment.setIsActive(department.getIsActive());
        }

        return departmentRepository.save(existingDepartment);
    }
}