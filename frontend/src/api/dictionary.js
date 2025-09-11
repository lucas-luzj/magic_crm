import request from '../utils/request'

// 字典API接口
export const dictionaryApi = {
  // 获取字典项列表（分页）
  getDictionaries(params = {}) {
    return request({
      url: '/dictionaries',
      method: 'get',
      params
    })
  },

  // 根据ID获取字典项
  getDictionaryById(id) {
    return request({
      url: `/dictionaries/${id}`,
      method: 'get'
    })
  },

  // 根据字典键获取字典项
  getDictionaryByKey(dictKey) {
    return request({
      url: `/dictionaries/by-key/${dictKey}`,
      method: 'get'
    })
  },

  // 根据字典类型获取字典项
  getDictionariesByType(dictType) {
    return request({
      url: `/dictionaries/by-type/${dictType}`,
      method: 'get'
    })
  },

  // 获取所有字典类型
  getDictTypes() {
    return request({
      url: '/dictionaries/types',
      method: 'get'
    })
  },

  // 创建字典项
  createDictionary(data) {
    return request({
      url: '/dictionaries',
      method: 'post',
      data
    })
  },

  // 更新字典项
  updateDictionary(id, data) {
    return request({
      url: `/dictionaries/${id}`,
      method: 'put',
      data
    })
  },

  // 删除字典项
  deleteDictionary(id) {
    return request({
      url: `/dictionaries/${id}`,
      method: 'delete'
    })
  },

  // 激活字典项
  activateDictionary(id) {
    return request({
      url: `/dictionaries/${id}/activate`,
      method: 'put'
    })
  },

  // 停用字典项
  deactivateDictionary(id) {
    return request({
      url: `/dictionaries/${id}/deactivate`,
      method: 'put'
    })
  },

  // 批量创建字典项
  createBatchDictionaries(data) {
    return request({
      url: '/dictionaries/batch',
      method: 'post',
      data
    })
  },

  // 根据字典类型删除所有字典项
  deleteByDictType(dictType) {
    return request({
      url: `/dictionaries/by-type/${dictType}`,
      method: 'delete'
    })
  },

  // 获取字典项的值
  getDictValue(dictType, dictKey) {
    return request({
      url: '/dictionaries/value',
      method: 'get',
      params: { dictType, dictKey }
    })
  },

  // 获取字典项选项（用于下拉选择）
  getDictOptions(dictType) {
    return request({
      url: `/dictionaries/options/${dictType}`,
      method: 'get'
    })
  }
}
