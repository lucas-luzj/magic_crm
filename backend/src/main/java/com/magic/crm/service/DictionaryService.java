package com.magic.crm.service;

import com.magic.crm.entity.Dictionary;
import com.magic.crm.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 字典表服务层
 */
@Service
@Transactional
public class DictionaryService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    /**
     * 创建字典项
     */
    public Dictionary createDictionary(Dictionary dictionary) {
        // 检查字典键是否已存在
        if (dictionaryRepository.existsByDictKey(dictionary.getDictKey())) {
            throw new RuntimeException("字典键已存在: " + dictionary.getDictKey());
        }

        // 检查字典类型和字典键组合是否已存在
        if (dictionaryRepository.existsByDictTypeAndDictKey(dictionary.getDictType(), dictionary.getDictKey())) {
            throw new RuntimeException("该字典类型下字典键已存在: " + dictionary.getDictKey());
        }

        return dictionaryRepository.save(dictionary);
    }

    /**
     * 更新字典项
     */
    public Dictionary updateDictionary(Long id, Dictionary dictionary) {
        Dictionary existingDictionary = dictionaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("字典项不存在: " + id));

        // 检查是否为系统字典项
        if (existingDictionary.getIsSystem()) {
            throw new RuntimeException("系统字典项不允许修改");
        }

        // 检查字典键是否被其他记录使用
        if (!existingDictionary.getDictKey().equals(dictionary.getDictKey()) &&
                dictionaryRepository.existsByDictKey(dictionary.getDictKey())) {
            throw new RuntimeException("字典键已存在: " + dictionary.getDictKey());
        }

        existingDictionary.setDictKey(dictionary.getDictKey());
        existingDictionary.setDictName(dictionary.getDictName());
        existingDictionary.setDictValue(dictionary.getDictValue());
        existingDictionary.setDictType(dictionary.getDictType());
        existingDictionary.setDescription(dictionary.getDescription());
        existingDictionary.setSortOrder(dictionary.getSortOrder());
        existingDictionary.setIsActive(dictionary.getIsActive());
        existingDictionary.setUpdatedBy(dictionary.getUpdatedBy());

        return dictionaryRepository.save(existingDictionary);
    }

    /**
     * 删除字典项
     */
    public void deleteDictionary(Long id) {
        Dictionary dictionary = dictionaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("字典项不存在: " + id));

        // 检查是否为系统字典项
        if (dictionary.getIsSystem()) {
            throw new RuntimeException("系统字典项不允许删除");
        }

        dictionaryRepository.deleteById(id);
    }

    /**
     * 根据ID查找字典项
     */
    public Optional<Dictionary> findById(Long id) {
        return dictionaryRepository.findById(id);
    }

    /**
     * 根据字典键查找字典项
     */
    public Optional<Dictionary> findByDictKey(String dictKey) {
        return dictionaryRepository.findByDictKey(dictKey);
    }

    /**
     * 根据字典类型和字典键查找字典项
     */
    public Optional<Dictionary> findByDictTypeAndDictKey(String dictType, String dictKey) {
        return dictionaryRepository.findByDictTypeAndDictKey(dictType, dictKey);
    }

    /**
     * 分页查询字典项
     */
    public Page<Dictionary> findDictionariesWithFilters(String dictType, String dictName,
            Boolean isSystem, Boolean isActive, Pageable pageable) {
        return dictionaryRepository.findDictionariesWithFilters(dictType, dictName, isSystem, isActive, pageable);
    }

    /**
     * 根据字典类型获取字典项
     */
    public List<Dictionary> findByDictType(String dictType) {
        return dictionaryRepository.findByDictTypeAndIsActiveTrueOrderBySortOrderAsc(dictType);
    }

    /**
     * 获取所有字典类型
     */
    public List<String> findAllDictTypes() {
        return dictionaryRepository.findAllDictTypes();
    }

    /**
     * 根据字典类型获取字典项（用于下拉选择）
     */
    public List<Dictionary> findActiveByDictType(String dictType) {
        return dictionaryRepository.findActiveByDictType(dictType);
    }

    /**
     * 激活字典项
     */
    public Dictionary activateDictionary(Long id) {
        Dictionary dictionary = dictionaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("字典项不存在: " + id));

        dictionary.setIsActive(true);
        return dictionaryRepository.save(dictionary);
    }

    /**
     * 停用字典项
     */
    public Dictionary deactivateDictionary(Long id) {
        Dictionary dictionary = dictionaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("字典项不存在: " + id));

        // 检查是否为系统字典项
        if (dictionary.getIsSystem()) {
            throw new RuntimeException("系统字典项不允许停用");
        }

        dictionary.setIsActive(false);
        return dictionaryRepository.save(dictionary);
    }

    /**
     * 批量创建字典项
     */
    public List<Dictionary> createBatchDictionaries(List<Dictionary> dictionaries) {
        // 检查字典键是否重复
        for (Dictionary dictionary : dictionaries) {
            if (dictionaryRepository.existsByDictKey(dictionary.getDictKey())) {
                throw new RuntimeException("字典键已存在: " + dictionary.getDictKey());
            }
        }

        return dictionaryRepository.saveAll(dictionaries);
    }

    /**
     * 根据字典类型删除所有字典项
     */
    public void deleteByDictType(String dictType) {
        List<Dictionary> dictionaries = dictionaryRepository.findByDictTypeAndIsActiveTrueOrderBySortOrderAsc(dictType);

        // 检查是否有系统字典项
        boolean hasSystemDictionary = dictionaries.stream().anyMatch(Dictionary::getIsSystem);
        if (hasSystemDictionary) {
            throw new RuntimeException("该字典类型包含系统字典项，不允许删除");
        }

        dictionaryRepository.deleteAll(dictionaries);
    }

    /**
     * 获取字典项的值（用于业务逻辑）
     */
    public String getDictValue(String dictType, String dictKey) {
        return dictionaryRepository.findByDictTypeAndDictKey(dictType, dictKey)
                .map(Dictionary::getDictValue)
                .orElse(null);
    }

    /**
     * 获取字典项列表（用于下拉选择）
     */
    public List<Dictionary> getDictOptions(String dictType) {
        return dictionaryRepository.findActiveByDictType(dictType);
    }
}
