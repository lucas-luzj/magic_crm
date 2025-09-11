package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.Dictionary;
import com.magic.crm.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 字典表控制器
 */
@RestController
@RequestMapping("/api/dictionaries")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 分页查询字典项
     */
    @GetMapping
    public ApiResponse<PageResponse<Dictionary>> getDictionaries(
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) Boolean isSystem,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Dictionary> result = dictionaryService.findDictionariesWithFilters(
                dictType, dictName, isSystem, isActive, pageable);

        return ApiResponse.success(new PageResponse<>(result));
    }

    /**
     * 根据ID获取字典项
     */
    @GetMapping("/{id}")
    public ApiResponse<Dictionary> getDictionaryById(@PathVariable Long id) {
        Optional<Dictionary> dictionary = dictionaryService.findById(id);
        return dictionary.map(ApiResponse::success)
                .orElse(ApiResponse.error("字典项不存在"));
    }

    /**
     * 根据字典键获取字典项
     */
    @GetMapping("/by-key/{dictKey}")
    public ApiResponse<Dictionary> getDictionaryByKey(@PathVariable String dictKey) {
        Optional<Dictionary> dictionary = dictionaryService.findByDictKey(dictKey);
        return dictionary.map(ApiResponse::success)
                .orElse(ApiResponse.error("字典项不存在"));
    }

    /**
     * 根据字典类型获取字典项
     */
    @GetMapping("/by-type/{dictType}")
    public ApiResponse<List<Dictionary>> getDictionariesByType(@PathVariable String dictType) {
        List<Dictionary> dictionaries = dictionaryService.findByDictType(dictType);
        return ApiResponse.success(dictionaries);
    }

    /**
     * 获取所有字典类型
     */
    @GetMapping("/types")
    public ApiResponse<List<String>> getDictTypes() {
        List<String> dictTypes = dictionaryService.findAllDictTypes();
        return ApiResponse.success(dictTypes);
    }

    /**
     * 创建字典项
     */
    @PostMapping
    public ApiResponse<Dictionary> createDictionary(@RequestBody Dictionary dictionary) {
        try {
            Dictionary savedDictionary = dictionaryService.createDictionary(dictionary);
            return ApiResponse.success("创建字典项成功", savedDictionary);
        } catch (Exception e) {
            return ApiResponse.error("创建字典项失败：" + e.getMessage());
        }
    }

    /**
     * 更新字典项
     */
    @PutMapping("/{id}")
    public ApiResponse<Dictionary> updateDictionary(@PathVariable Long id, @RequestBody Dictionary dictionary) {
        try {
            Dictionary updatedDictionary = dictionaryService.updateDictionary(id, dictionary);
            return ApiResponse.success("更新字典项成功", updatedDictionary);
        } catch (Exception e) {
            return ApiResponse.error("更新字典项失败：" + e.getMessage());
        }
    }

    /**
     * 删除字典项
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDictionary(@PathVariable Long id) {
        try {
            dictionaryService.deleteDictionary(id);
            return ApiResponse.success("删除字典项成功");
        } catch (Exception e) {
            return ApiResponse.error("删除字典项失败：" + e.getMessage());
        }
    }

    /**
     * 激活字典项
     */
    @PutMapping("/{id}/activate")
    public ApiResponse<Dictionary> activateDictionary(@PathVariable Long id) {
        try {
            Dictionary dictionary = dictionaryService.activateDictionary(id);
            return ApiResponse.success("激活字典项成功", dictionary);
        } catch (Exception e) {
            return ApiResponse.error("激活字典项失败：" + e.getMessage());
        }
    }

    /**
     * 停用字典项
     */
    @PutMapping("/{id}/deactivate")
    public ApiResponse<Dictionary> deactivateDictionary(@PathVariable Long id) {
        try {
            Dictionary dictionary = dictionaryService.deactivateDictionary(id);
            return ApiResponse.success("停用字典项成功", dictionary);
        } catch (Exception e) {
            return ApiResponse.error("停用字典项失败：" + e.getMessage());
        }
    }

    /**
     * 批量创建字典项
     */
    @PostMapping("/batch")
    public ApiResponse<List<Dictionary>> createBatchDictionaries(@RequestBody List<Dictionary> dictionaries) {
        try {
            List<Dictionary> savedDictionaries = dictionaryService.createBatchDictionaries(dictionaries);
            return ApiResponse.success("批量创建字典项成功", savedDictionaries);
        } catch (Exception e) {
            return ApiResponse.error("批量创建字典项失败：" + e.getMessage());
        }
    }

    /**
     * 根据字典类型删除所有字典项
     */
    @DeleteMapping("/by-type/{dictType}")
    public ApiResponse<Void> deleteByDictType(@PathVariable String dictType) {
        try {
            dictionaryService.deleteByDictType(dictType);
            return ApiResponse.success("删除字典类型成功");
        } catch (Exception e) {
            return ApiResponse.error("删除字典类型失败：" + e.getMessage());
        }
    }

    /**
     * 获取字典项的值（用于业务逻辑）
     */
    @GetMapping("/value")
    public ApiResponse<String> getDictValue(@RequestParam String dictType, @RequestParam String dictKey) {
        String value = dictionaryService.getDictValue(dictType, dictKey);
        return ApiResponse.success(value);
    }

    /**
     * 获取字典项选项（用于下拉选择）
     */
    @GetMapping("/options/{dictType}")
    public ApiResponse<List<Dictionary>> getDictOptions(@PathVariable String dictType) {
        List<Dictionary> options = dictionaryService.getDictOptions(dictType);
        return ApiResponse.success(options);
    }
}
