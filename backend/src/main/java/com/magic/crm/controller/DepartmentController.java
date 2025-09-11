package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.DepartmentDTO;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.Department;
import com.magic.crm.service.DepartmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 获取部门树形结构
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getDepartmentTree() {
        List<DepartmentDTO> departmentTree = departmentService.getDepartmentTree();
        return ResponseEntity.ok(ApiResponse.success(departmentTree));
    }

    /**
     * 获取所有启用的部门列表
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getActiveDepartments() {
        List<DepartmentDTO> departments = departmentService.getActiveDepartments();
        return ResponseEntity.ok(ApiResponse.success(departments));
    }

    /**
     * 根据ID获取部门详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    /**
     * 创建部门
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return ResponseEntity.ok(ApiResponse.success("部门创建成功", createdDepartment));
    }

    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(ApiResponse.success("部门更新成功", updatedDepartment));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("部门删除成功"));
    }

    /**
     * 根据名称搜索部门
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> searchDepartments(@RequestParam String name) {
        List<DepartmentDTO> departments = departmentService.searchDepartmentsByName(name);
        return ResponseEntity.ok(ApiResponse.success(departments));
    }

    // ========== 为Dept.js提供的POST方法 ==========

    /**
     * 分页查询部门列表 (POST方法)
     * 支持关键词搜索
     */
    @PostMapping("/list")
    public PageResponse<Department> list(String keyword, Integer pid, Integer pageIndex, Integer pageSize) {
        return new PageResponse(departmentService.findDepartments(keyword, pid, pageIndex, pageSize));
    }

    /**
     * 根据ID获取部门详情 (POST方法)
     */
    @PostMapping("/get")
    public Department get(Long id) {
        if (id == null) {
            throw new RuntimeException("部门ID不能为空");
        }
        return departmentService.findById(id);
    }

    /**
     * 新增或更新部门 (POST方法)
     * id=0时新增，id>0时更新
     */
    @PostMapping("/update")
    public String update(Department department) {
        if (department == null) {
            throw new RuntimeException("部门信息不能为空");
        }

        var dept = departmentService.saveOrUpdateDepartment(department);
        if (department.getId() == null || department.getId() == 0) {
            return "OK:" + dept.getId();
        }

        return "OK";
    }

    /**
     * 删除部门 (POST方法)
     */
    @PostMapping("/delete")
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("部门ID不能为空");
        }
        departmentService.deleteDepartment(id);
    }
}