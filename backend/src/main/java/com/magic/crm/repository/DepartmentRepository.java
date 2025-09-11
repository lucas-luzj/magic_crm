package com.magic.crm.repository;

import com.magic.crm.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门数据访问层
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 根据父部门ID查询子部门列表
     */
    List<Department> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 查询根部门列表（父部门ID为空）
     */
    List<Department> findByParentIdIsNullOrderBySortOrderAsc();

    /**
     * 根据部门编码查询部门
     */
    Optional<Department> findByCode(String code);

    /**
     * 根据部门名称模糊查询
     */
    List<Department> findByNameContainingIgnoreCaseOrderBySortOrderAsc(String name);

    /**
     * 查询启用状态的部门
     */
    List<Department> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 查询指定部门的所有子部门（递归）
     */
    @Query(value = """
            WITH RECURSIVE dept_tree AS (
                SELECT id, name, code, parent_id, manager_id, description,
                       sort_order, is_active, created_at, updated_at, created_by, updated_by
                FROM departments
                WHERE id = :departmentId
                UNION ALL
                SELECT d.id, d.name, d.code, d.parent_id, d.manager_id, d.description,
                       d.sort_order, d.is_active, d.created_at, d.updated_at, d.created_by, d.updated_by
                FROM departments d
                INNER JOIN dept_tree dt ON d.parent_id = dt.id
            )
            SELECT * FROM dept_tree WHERE id != :departmentId ORDER BY sort_order
            """, nativeQuery = true)
    List<Department> findAllChildrenByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 检查部门编码是否存在（排除指定ID）
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * 统计部门下的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.departmentId = :departmentId")
    long countUsersByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 分页查询，支持按部门名称、编码搜索
     */
    @Query("SELECT d FROM Department d WHERE " +
            "((:parentId IS NULL and d.parentId IS NULL) OR d.parentId = :parentId) AND (" +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%', :keyword, '%'))))" +
            "ORDER BY d.sortOrder ASC")
    Page<Department> findByKeyword(@Param("keyword") String keyword, Integer parentId, Pageable pageable);
}