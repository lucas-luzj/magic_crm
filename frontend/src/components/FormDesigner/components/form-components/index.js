import { ComponentManager } from '../../core/ComponentManager'

import TabsComponent from './TabsComponent.vue'
import RowContainer from './RowContainer.vue'
import RuntimeRow from '../runtime/RuntimeRow.vue'
import RuntimeTabs from '../runtime/RuntimeTabs.vue'
/**
 * 注册所有基础表单组件
 */
export function registerBasicComponents() {
  // 输入框组件
  ComponentManager.registerComponent('text', {
    name: '单行输入',
    description: '单行文本输入框',
    icon: 'Edit',
    category: 'basic',
    label: '单行输入',
    field: 'input',
    placeholder: '请输入内容',
    span: 24,
    componentName: 'el-input',
    defaultProps: {
      inputType: 'text',
      clearable: true,
      showWordLimit: false,
      showPassword: false,
      maxLength: null,
      minLength: null
    },
    properties: [
      {
        key: 'inputType',
        label: '输入类型',
        type: 'select',
        options: [
          { label: '文本', value: 'text' },
          { label: '密码', value: 'password' },
          { label: '邮箱', value: 'email' },
          { label: '电话', value: 'tel' },
          { label: '网址', value: 'url' }
        ]
      },
      {
        key: 'clearable',
        label: '可清空',
        type: 'switch'
      },
      {
        key: 'showWordLimit',
        label: '显示字数统计',
        type: 'switch'
      },
      {
        key: 'showPassword',
        label: '显示密码切换',
        type: 'switch'
      },
      {
        key: 'maxLength',
        label: '最大长度',
        type: 'number',
        min: 0
      },
      {
        key: 'minLength',
        label: '最小长度',
        type: 'number',
        min: 0
      }
    ]
  })

  // 多行输入框组件
  ComponentManager.registerComponent('textarea', {
    name: '多行输入',
    description: '多行文本输入框',
    icon: 'Document',
    category: 'basic',
    label: '多行输入',
    field: 'textarea',
    placeholder: '请输入内容',
    span: 24,
    componentName: 'el-input',
    defaultProps: {
      type: 'textarea',
      rows: 4,
      showWordLimit: false,
      resize: 'vertical',
      maxLength: null,
      minLength: null
    },
    properties: [
      {
        key: 'rows',
        label: '行数',
        type: 'number',
        min: 2,
        max: 20
      },
      {
        key: 'showWordLimit',
        label: '显示字数统计',
        type: 'switch'
      },
      {
        key: 'resize',
        label: '缩放方向',
        type: 'select',
        options: [
          { label: '无', value: 'none' },
          { label: '垂直', value: 'vertical' },
          { label: '水平', value: 'horizontal' },
          { label: '全部', value: 'both' }
        ]
      },
      {
        key: 'maxLength',
        label: '最大长度',
        type: 'number',
        min: 0
      },
      {
        key: 'minLength',
        label: '最小长度',
        type: 'number',
        min: 0
      }
    ]
  })

  // 数字输入框组件
  ComponentManager.registerComponent('number', {
    name: '数字输入',
    description: '数字输入框',
    icon: 'Tickets',
    category: 'basic',
    label: '数字输入',
    field: 'number',
    placeholder: '请输入数字',
    span: 24,
    componentName: 'el-input-number',
    defaultProps: {
      min: null,
      max: null,
      step: 1,
      precision: null,
      controls: true,
      controlsPosition: 'right'
    },
    properties: [
      {
        key: 'min',
        label: '最小值',
        type: 'number'
      },
      {
        key: 'max',
        label: '最大值',
        type: 'number'
      },
      {
        key: 'step',
        label: '步长',
        type: 'number',
        min: 0.01
      },
      {
        key: 'precision',
        label: '精度',
        type: 'number',
        min: 0,
        max: 10
      },
      {
        key: 'controls',
        label: '显示控制按钮',
        type: 'switch'
      },
      {
        key: 'controlsPosition',
        label: '控制按钮位置',
        type: 'select',
        options: [
          { label: '右侧', value: 'right' },
          { label: '两侧', value: '' }
        ]
      }
    ]
  })

  // 选择器组件
  ComponentManager.registerComponent('select', {
    name: '下拉选择',
    description: '下拉选择器',
    icon: 'ArrowDown',
    category: 'basic',
    label: '下拉选择',
    field: 'select',
    placeholder: '请选择',
    span: 24,
    componentName: 'el-select',
    defaultProps: {
      multiple: false,
      clearable: true,
      filterable: false,
      options: [
        { label: '选项1', value: 'option1' },
        { label: '选项2', value: 'option2' },
        { label: '选项3', value: 'option3' }
      ]
    },
    properties: [
      {
        key: 'multiple',
        label: '多选',
        type: 'switch'
      },
      {
        key: 'clearable',
        label: '可清空',
        type: 'switch'
      },
      {
        key: 'filterable',
        label: '可搜索',
        type: 'switch'
      },
      {
        key: 'options',
        label: '选项配置',
        type: 'options'
      }
    ]
  })

  // 单选框组件
  ComponentManager.registerComponent('radio', {
    name: '单选框',
    description: '单选框组',
    icon: 'CircleCheck',
    category: 'basic',
    label: '单选框',
    field: 'radio',
    span: 24,
    componentName: 'el-radio-group',
    defaultProps: {
      options: [
        { label: '选项1', value: 'option1' },
        { label: '选项2', value: 'option2' },
        { label: '选项3', value: 'option3' }
      ]
    },
    properties: [
      {
        key: 'options',
        label: '选项配置',
        type: 'options'
      }
    ]
  })

  // 复选框组件
  ComponentManager.registerComponent('checkbox', {
    name: '复选框',
    description: '复选框组',
    icon: 'Select',
    category: 'basic',
    label: '复选框',
    field: 'checkbox',
    span: 24,
    componentName: 'el-checkbox-group',
    defaultProps: {
      options: [
        { label: '选项1', value: 'option1' },
        { label: '选项2', value: 'option2' },
        { label: '选项3', value: 'option3' }
      ]
    },
    properties: [
      {
        key: 'options',
        label: '选项配置',
        type: 'options'
      }
    ]
  })

  // 日期选择器组件
  ComponentManager.registerComponent('date', {
    name: '日期选择',
    description: '日期选择器',
    icon: 'Calendar',
    category: 'basic',
    label: '日期选择',
    field: 'date',
    placeholder: '请选择日期',
    span: 24,
    componentName: 'el-date-picker',
    defaultProps: {
      type: 'date',
      clearable: true,
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD'
    },
    properties: [
      {
        key: 'type',
        label: '选择类型',
        type: 'select',
        options: [
          { label: '日期', value: 'date' },
          { label: '日期时间', value: 'datetime' },
          { label: '日期范围', value: 'daterange' },
          { label: '日期时间范围', value: 'datetimerange' },
          { label: '月份', value: 'month' },
          { label: '年份', value: 'year' }
        ]
      },
      {
        key: 'clearable',
        label: '可清空',
        type: 'switch'
      },
      {
        key: 'format',
        label: '显示格式',
        type: 'input',
        placeholder: 'YYYY-MM-DD'
      },
      {
        key: 'valueFormat',
        label: '值格式',
        type: 'input',
        placeholder: 'YYYY-MM-DD'
      }
    ]
  })

   // 日期时间选择器组件
  ComponentManager.registerComponent('datetime', {
    name: '日期时间选择',
    description: '日期时间选择',
    icon: 'Timer',
    category: 'basic',
    label: '日期时间选择',
    field: 'datetime',
    placeholder: '请选择日期时间',
    span: 24,
    componentName: 'el-date-picker',
    defaultProps: {
      type: 'datetime',
      clearable: true,
      format: 'YYYY-MM-DD HH:mm',
      valueFormat: 'YYYY-MM-DD HH:mm'
    },
    properties: [
      {
        key: 'clearable',
        label: '可清空',
        type: 'switch'
      },
      {
        key: 'format',
        label: '显示格式',
        type: 'input',
        placeholder: 'YYYY-MM-DD HH:mm'
      },
      {
        key: 'valueFormat',
        label: '值格式',
        type: 'input',
        placeholder: 'YYYY-MM-DD HH:mm'
      }
    ]
  })

  // 文件上传组件
  ComponentManager.registerComponent('file', {
    name: '文件上传',
    description: '文件上传',
    icon: 'Upload',
    category: 'basic',
    label: '文件上传',
    field: 'file',
    placeholder: '请选择文件上传',
    span: 24,
    componentName: 'FileUpLoader',
    defaultProps: {
      max:1,
    },
    properties: [
      {
        key: 'fileAccept',
        label: '可上传文件类型',
        type: 'select',
        options: [
          { label: '文档', value: '.doc,.docx,.xls,.xlsx,.ppt,.pptx,.pdf' },
          { label: '图片', value: '.jpg,.png,.gif' },
          { label: '视频', value: '.mp4,.avi,.rmvb' },
          { label: '音频', value: '.mp3,.wav' },
          { label: '压缩文件', value: '.rar,.zip' },
        ]
      },
      {
        key: 'max',
        label: '最大文件数',
        type: 'number'
      },
    ]
  })

 // 图片上传组件
  ComponentManager.registerComponent('photo', {
    name: '上传图片',
    description: '上传图片',
    icon: 'Picture',
    category: 'basic',
    label: '图片上传',
    field: 'photo',
    placeholder: '请选择图片上传',
    span: 24,
    componentName: 'ImageUploader',
    defaultProps: {
      max:1,
    },
    properties: [
      {
        key: 'max',
        label: '最大文件数',
        type: 'number'
      },
    ]
  })

  // Tabs组件
  ComponentManager.registerComponent('tabs', {
    name: '标签页',
    description: '标签页容器',
    icon: 'CollectionTag',
    category: 'layout',
    span: 24,
    runComponent: RuntimeTabs,
    defaultProps: {
      tabs: [
        { label: '标签1' },
        { label: '标签2' },
        { label: '标签3' }
      ]
    },
    properties: [
      {
        key: 'tabs',
        label: '标签配置',
        type: 'tabs'
      }
    ]
  }, TabsComponent)

  // 行容器组件
  ComponentManager.registerComponent('row', {
    name: '行容器',
    description: '行布局容器，支持24栅格系统',
    icon: 'Grid',
    category: 'layout',
    label: '行容器',
    span: 24,
    runComponent:RuntimeRow,
    defaultProps: {
      gutter: 16,
      children: []
    },
    properties: [
      {
        key: 'gutter',
        label: '栅格间隔',
        type: 'number',
        min: 0,
        max: 48
      },
      {
        key: 'label',
        label: '容器标题',
        type: 'input',
        placeholder: '行容器'
      }
    ]
  }, RowContainer)
}

// 自动注册组件
registerBasicComponents()