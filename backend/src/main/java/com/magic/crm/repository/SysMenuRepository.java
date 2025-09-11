package com.magic.crm.repository;

import com.magic.crm.entity.SysMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {

    List<SysMenu> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<SysMenu> findByMenuTypeOrderBySortOrderAsc(String menuType);

    List<SysMenu> findByStatusTrueOrderBySortOrderAsc();

    List<SysMenu> findByParentIdAndStatusTrueOrderBySortOrderAsc(Long parentId);

    @Query("SELECT m FROM SysMenu m WHERE m.parentId = 0 AND m.status = true ORDER BY m.sortOrder ASC")
    List<SysMenu> findRootMenus();

    @Query("SELECT m FROM SysMenu m WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(m.menuName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.path) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.permissionCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SysMenu> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByPermissionCode(String permissionCode);

    List<SysMenu> findByParentId(Long parentId);

    boolean existsByPermissionCodeAndIdNot(String permissionCode, Long id);
}