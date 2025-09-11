package com.magic.crm.service;

import com.magic.crm.entity.FormCategory;
import com.magic.crm.repository.FormCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 表单分类服务层
 */
@Service
@Transactional
public class FormCategoryService {

    @Autowired
    private FormCategoryRepository formCategoryRepository;

    /**
     * 创建表单分类
     */
    public FormCategory createFormCategory(FormCategory category) {
        // 检查分类键是否已存在
        if (formCategoryRepository.existsByCategoryKey(category.getCategoryKey())) {
            throw new RuntimeException("分类键已存在: " + category.getCategoryKey());
        }

        // 检查分类名称是否已存在
        if (formCategoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("分类名称已存在: " + category.getCategoryName());
        }

        return formCategoryRepository.save(category);
    }

    /**
     * 更新表单分类
     */
    public FormCategory updateFormCategory(Long id, FormCategory category) {
        FormCategory existingCategory = formCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单分类不存在: " + id));

        // 检查分类键是否被其他分类使用
        if (!existingCategory.getCategoryKey().equals(category.getCategoryKey()) &&
                formCategoryRepository.existsByCategoryKey(category.getCategoryKey())) {
            throw new RuntimeException("分类键已存在: " + category.getCategoryKey());
        }

        // 检查分类名称是否被其他分类使用
        if (!existingCategory.getCategoryName().equals(category.getCategoryName()) &&
                formCategoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("分类名称已存在: " + category.getCategoryName());
        }

        existingCategory.setCategoryKey(category.getCategoryKey());
        existingCategory.setCategoryName(category.getCategoryName());
        existingCategory.setCategoryDescription(category.getCategoryDescription());
        existingCategory.setParentId(category.getParentId());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setIsActive(category.getIsActive());

        return formCategoryRepository.save(existingCategory);
    }

    /**
     * 删除表单分类
     */
    public void deleteFormCategory(Long id) {
        // 检查分类是否存在
        if (!formCategoryRepository.existsById(id)) {
            throw new RuntimeException("表单分类不存在: " + id);
        }

        // 检查是否有子分类
        List<FormCategory> children = formCategoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("该分类下还有子分类，无法删除");
        }

        formCategoryRepository.deleteById(id);
    }

    /**
     * 根据ID查找表单分类
     */
    public Optional<FormCategory> findById(Long id) {
        return formCategoryRepository.findById(id);
    }

    /**
     * 根据分类键查找表单分类
     */
    public Optional<FormCategory> findByCategoryKey(String categoryKey) {
        return formCategoryRepository.findByCategoryKey(categoryKey);
    }

    /**
     * 获取所有启用的分类
     */
    public List<FormCategory> findAllActive() {
        return formCategoryRepository.findAllActiveOrderBySortOrder();
    }

    /**
     * 获取根分类
     */
    public List<FormCategory> findRootCategories() {
        return formCategoryRepository.findRootCategories();
    }

    /**
     * 根据父级ID查找子分类
     */
    public List<FormCategory> findByParentId(Long parentId) {
        return formCategoryRepository.findByParentId(parentId);
    }

    /**
     * 获取分类树
     * 由于PostgreSQL不支持START WITH...CONNECT BY，这里使用递归方式构建树
     */
    public List<FormCategory> getCategoryTree(Long parentId) {
        List<FormCategory> rootCategories = formCategoryRepository.findCategoryTree(parentId);
        return buildCategoryTree(rootCategories);
    }

    /**
     * 递归构建分类树
     */
    private List<FormCategory> buildCategoryTree(List<FormCategory> categories) {
        for (FormCategory category : categories) {
            List<FormCategory> children = formCategoryRepository.findByParentIdAndIsActive(category.getId(), true);
            if (!children.isEmpty()) {
                category.setChildren(buildCategoryTree(children));
            }
        }
        return categories;
    }

    /**
     * 激活分类
     */
    public FormCategory activateCategory(Long id) {
        FormCategory category = formCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单分类不存在: " + id));

        category.setIsActive(true);
        return formCategoryRepository.save(category);
    }

    /**
     * 停用分类
     */
    public FormCategory deactivateCategory(Long id) {
        FormCategory category = formCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单分类不存在: " + id));

        category.setIsActive(false);
        return formCategoryRepository.save(category);
    }

    /**
     * 移动分类到新的父级
     */
    public FormCategory moveCategory(Long id, Long newParentId) {
        FormCategory category = formCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单分类不存在: " + id));

        // 检查不能移动到自己的子分类下
        if (newParentId != null) {
            List<FormCategory> descendants = formCategoryRepository.findByParentIdAndIsActive(id, true);
            boolean isDescendant = descendants.stream()
                    .anyMatch(desc -> desc.getId().equals(newParentId));
            if (isDescendant) {
                throw new RuntimeException("不能将分类移动到自己的子分类下");
            }
        }

        category.setParentId(newParentId);
        return formCategoryRepository.save(category);
    }

    /**
     * 更新分类排序
     */
    public FormCategory updateCategorySortOrder(Long id, Integer sortOrder) {
        FormCategory category = formCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单分类不存在: " + id));

        category.setSortOrder(sortOrder);
        return formCategoryRepository.save(category);
    }
}
