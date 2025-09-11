import { markRaw, defineAsyncComponent } from 'vue'
import FormComponent from './FormComponent.vue'

// 组件渲染器映射
const componentRenderers = new Map()

// 组件配置映射
const componentConfigs = new Map()

// 组件分类
const componentCategories = [
  {
    name: 'basic',
    title: '基础组件',
    expanded: true,
    components: []
  },
  {
    name: 'advanced',
    title: '高级组件',
    expanded: false,
    components: []
  },
  {
    name: 'layout',
    title: '布局组件',
    expanded: false,
    components: []
  }
]

/**
 * 组件管理器
 */
export class ComponentManager {
  /**
   * 注册组件
   * @param {string} cmpType 组件类型
   * @param {Object} config 组件配置
   * @param {Object} [renderer] 组件渲染器，如果未提供，则使用通用渲染器
   */
  static registerComponent(cmpType, config, renderer) {
    componentConfigs.set(cmpType, config)

    
    let finalRenderer;
    if (renderer) {
      finalRenderer = markRaw(renderer);
    } else {
      // 对于没有自定义渲染器的组件，直接使用通用的 FormComponent。
      // FormComponent 会根据传入的 config.cmpType 自行决定渲染哪个具体的输入组件。
      finalRenderer = markRaw(FormComponent);
    }
    componentRenderers.set(cmpType, finalRenderer);

    const category = componentCategories.find(cat => cat.name === config.category)
    if (category) {
      const existingIndex = category.components.findIndex(comp => comp.cmpType === cmpType)
      if (existingIndex >= 0) {
        // 更新现有组件
        category.components[existingIndex] = {
          cmpType,
          name: config.name,
          description: config.description,
          icon: config.icon,
          category: config.category
        }
      } else {
        // 添加新组件
        category.components.push({
          cmpType,
          name: config.name,
          description: config.description,
          icon: config.icon,
          category: config.category
        })
      }
    }
  }
  
  /**
   * 获取组件配置
   * @param {string} cmpType 组件类型
   * @returns {Object|null} 组件配置
   */
  static getComponentConfig(cmpType) {
    return componentConfigs.get(cmpType) || null
  }
  
  /**
   * 获取组件渲染器
   * @param {string} cmpType 组件类型
   * @returns {Object|null} 组件渲染器
   */
  static getRenderer(cmpType) {
    return componentRenderers.get(cmpType) || null
  }
  
  /**
   * 创建组件实例
   * @param {string} cmpType 组件类型
   * @returns {Object|null} 组件实例配置
   */
  static createComponent(cmpType) {
    const config = componentConfigs.get(cmpType)
    if (!config) return null
    
    // 创建组件实例的默认配置
    const instance = {
      cmpType,
      componentName: config.componentName, // 复制 componentName
      name: config.name,
      label: config.label || config.name,
      field: config.field || `field_${Date.now()}`,
      span: config.span || 24,
      offset: 0,
      required: false,
      disabled: false,
      placeholder: config.placeholder || '',
      ...config.defaultProps
    }
    
    // 如果有选项类型的属性，初始化默认选项
    if (config.properties) {
      config.properties.forEach(prop => {
        if (prop.cmpType === 'options' && !instance[prop.key]) {
          instance[prop.key] = prop.defaultValue || [
            { label: '选项1', value: 'option1' },
            { label: '选项2', value: 'option2' }
          ]
        }
      })
    }
    
    return instance
  }
  
  /**
   * 获取所有分类
   * @returns {Array} 分类列表
   */
  static getCategories() {
    return componentCategories
  }
  
  /**
   * 获取指定分类的组件
   * @param {string} categoryName 分类名称
   * @returns {Array} 组件列表
   */
  static getCategoryComponents(categoryName) {
    const category = componentCategories.find(cat => cat.name === categoryName)
    return category ? category.components : []
  }
  
  /**
   * 搜索组件
   * @param {string} keyword 搜索关键词
   * @returns {Array} 匹配的组件列表
   */
  static searchComponents(keyword) {
    const results = []
    const lowerKeyword = keyword.toLowerCase()
    
    componentCategories.forEach(category => {
      category.components.forEach(component => {
        if (
          component.name.toLowerCase().includes(lowerKeyword) ||
          component.description.toLowerCase().includes(lowerKeyword)
        ) {
          results.push({
            ...component,
            categoryTitle: category.title
          })
        }
      })
    })
    
    return results
  }
  
  /**
   * 批量注册组件
   * @param {Array} components 组件列表
   */
  static registerComponents(components) {
    components.forEach(({ cmpType, config, renderer }) => {
      this.registerComponent(cmpType, config, renderer)
    })
  }
  
  /**
   * 注销组件
   * @param {string} cmpType 组件类型
   */
  static unregisterComponent(cmpType) {
    // 从配置中移除
    componentConfigs.delete(cmpType)
    componentRenderers.delete(cmpType)
    
    // 从分类中移除
    componentCategories.forEach(category => {
      const index = category.components.findIndex(comp => comp.cmpType === cmpType)
      if (index >= 0) {
        category.components.splice(index, 1)
      }
    })
  }
  
  /**
   * 获取所有已注册的组件类型
   * @returns {Array} 组件类型列表
   */
  static getRegisteredcmpTypes() {
    return Array.from(componentConfigs.keys())
  }
  
  /**
   * 检查组件是否已注册
   * @param {string} cmpType 组件类型
   * @returns {boolean} 是否已注册
   */
  static isRegistered(cmpType) {
    return componentConfigs.has(cmpType)
  }
}