package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.User;
import com.magic.crm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理Controller
 * 提供用户的CRUD操作
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表 (RESTful API)
     * 支持关键词搜索（用户名、邮箱、全名）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<User>>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String role) {
        PageResponse<User> pageResponse = new PageResponse<>(userService.findUsers(keyword, departmentId, page, size));
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * 分页查询用户列表 (POST方法，兼容旧接口)
     * 支持关键词搜索（用户名、邮箱、全名）
     */
    @PostMapping("/list")
    public PageResponse<User> list(String keyword, Integer departmentId, Integer pageIndex, Integer pageSize) {
        return new PageResponse(userService.findUsers(keyword, departmentId, pageIndex, pageSize));
    }

    /**
     * 根据ID获取用户详情 (RESTful API)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 根据ID获取用户详情 (POST方法，兼容旧接口)
     */
    @PostMapping("/get")
    public User get(Long id) {
        if (id == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return userService.findById(id);
    }

    /**
     * 创建用户 (RESTful API)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.saveOrUpdateUser(user);
        return ResponseEntity.ok(ApiResponse.success("用户创建成功", createdUser));
    }

    /**
     * 更新用户 (RESTful API)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.saveOrUpdateUser(user);
        return ResponseEntity.ok(ApiResponse.success("用户更新成功", updatedUser));
    }

    /**
     * 重置密码 (RESTful API)
     */
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String password = request.get("password");
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码不能为空"));
        }
        userService.changePassword(id, password);
        return ResponseEntity.ok(ApiResponse.success("密码重置成功"));
    }

    /**
     * 新增或更新用户 (POST方法，兼容旧接口)
     * id=0时新增，id>0时更新
     */
    @PostMapping("/update")
    public String update(User user) {
        if (user == null) {
            throw new RuntimeException("用户信息不能为空");
        }

        if (user.getId() == null || user.getId() == 0) {
            user.setPassword("123456");
        }

        var u1 = userService.saveOrUpdateUser(user);
        if (user.getId() == null || user.getId() == 0)
            return "OK:" + u1.getId();

        return "OK";
    }

    /**
     * 删除用户 (RESTful API)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("用户删除成功"));
    }

    /**
     * 删除用户 (POST方法，兼容旧接口)
     */
    @PostMapping("/delete")
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("用户ID不能为空");
        }
        userService.deleteUser(id);
    }
}