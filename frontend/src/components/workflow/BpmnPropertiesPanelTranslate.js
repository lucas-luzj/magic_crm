/**
 * BPMN.js 属性面板中文翻译配置
 * 专门用于翻译属性面板中的各种字段和标签
 */

// 属性面板中文翻译映射表
const propertiesPanelTranslations = {
  // 基础属性
  'General': '通用',
  'Details': '详情',
  'Documentation': '文档',
  'Element Documentation': '元素文档',
  'Process Documentation': '流程文档',
  
  // 标识信息
  'Id': '标识',
  'Name': '名称',
  'Process Id': '流程标识',
  'Process Name': '流程名称',
  'Executable': '可执行',
  'Version Tag': '版本标签',
  'History Time To Live': '历史生存时间',
  'Startable in Tasklist': '可在任务列表中启动',
  
  // 表单相关
  'Forms': '表单',
  'Form Key': '表单键',
  'Form Fields': '表单字段',
  'Business Key': '业务键',
  'Form Data': '表单数据',
  'Form': '表单',
  'Form Type': '表单类型',
  'Form Reference': '表单引用',
  'Embedded or External Task Forms': '嵌入式或外部任务表单',
  'Generated Task Forms': '生成的任务表单',
  'Custom Task Forms': '自定义任务表单',
  
  // 任务分配
  'Assignment': '分配',
  'Assignee': '受让人',
  'Candidate Users': '候选用户',
  'Candidate Groups': '候选组',
  'Due Date': '到期日期',
  'Follow Up Date': '跟进日期',
  'Priority': '优先级',
  
  // 监听器
  'Listeners': '监听器',
  'Execution Listeners': '执行监听器',
  'Task Listeners': '任务监听器',
  'Event': '事件',
  'Type': '类型',
  'Java Class': 'Java类',
  'Expression': '表达式',
  'Delegate Expression': '委托表达式',
  'Script': '脚本',
  'Script Format': '脚本格式',
  'Script Type': '脚本类型',
  'Inline Script': '内联脚本',
  'External Script': '外部脚本',
  'Resource': '资源',
  'Event Type': '事件类型',
  'Listener Type': '监听器类型',
  
  // 输入输出
  'Input/Output': '输入/输出',
  'Input Parameters': '输入参数',
  'Output Parameters': '输出参数',
  'Local Variable Name': '本地变量名',
  'Local Variable Assignment': '本地变量赋值',
  'Parameter': '参数',
  'Parameters': '参数',
  'Name': '名称',
  'Value': '值',
  'Add Parameter': '添加参数',
  'Remove Parameter': '删除参数',
  
  // 扩展属性
  'Extensions': '扩展',
  'Properties': '属性',
  'Property': '属性',
  'Extension Properties': '扩展属性',
  'Add Property': '添加属性',
  'Remove Property': '删除属性',
  
  // 条件和表达式
  'Condition': '条件',
  'Condition Type': '条件类型',
  'Condition Expression': '条件表达式',
  'Variable Name': '变量名',
  'Variable Event': '变量事件',
  'Variable Events': '变量事件',
  'Expression Language': '表达式语言',
  
  // 多实例
  'Multi Instance': '多实例',
  'Loop Cardinality': '循环基数',
  'Collection': '集合',
  'Element Variable': '元素变量',
  'Completion Condition': '完成条件',
  'Sequential': '顺序',
  'Parallel': '并行',
  'Multi Instance Type': '多实例类型',
  
  // 异步配置
  'Asynchronous Continuations': '异步延续',
  'Asynchronous Before': '之前异步',
  'Asynchronous After': '之后异步',
  'Exclusive': '排他',
  'Job Priority': '作业优先级',
  'Retry Time Cycle': '重试时间周期',
  'Job Configuration': '作业配置',
  
  // 外部任务
  'External Task Configuration': '外部任务配置',
  'Topic': '主题',
  'Task Priority': '任务优先级',
  'External Task': '外部任务',
  
  // 字段注入
  'Field Injections': '字段注入',
  'Field Name': '字段名',
  'Field Type': '字段类型',
  'String': '字符串',
  'String Value': '字符串值',
  'Expression Value': '表达式值',
  'Add Field': '添加字段',
  'Remove Field': '删除字段',
  
  // 连接器
  'Connector': '连接器',
  'Connector Id': '连接器标识',
  'Connector Configuration': '连接器配置',
  
  // 实现配置
  'Implementation': '实现',
  'Implementation Type': '实现类型',
  'Class': '类',
  'External': '外部',
  'Decision Ref': '决策引用',
  'Decision Ref Binding': '决策引用绑定',
  'Decision Ref Version': '决策引用版本',
  'Decision Ref Version Tag': '决策引用版本标签',
  'Decision Ref Tenant Id': '决策引用租户标识',
  'Map Decision Result': '映射决策结果',
  'Result Variable': '结果变量',
  'DMN Implementation': 'DMN实现',
  
  // 调用活动
  'Called Element': '被调用元素',
  'Called Element Type': '被调用元素类型',
  'Called Element Binding': '被调用元素绑定',
  'Called Element Version': '被调用元素版本',
  'Called Element Version Tag': '被调用元素版本标签',
  'Called Element Tenant Id': '被调用元素租户标识',
  'Case Ref': '案例引用',
  'Case Ref Binding': '案例引用绑定',
  'Case Ref Version': '案例引用版本',
  'Case Ref Tenant Id': '案例引用租户标识',
  'Variable Mapping': '变量映射',
  'In Mapping': '输入映射',
  'Out Mapping': '输出映射',
  'Business Key Expression': '业务键表达式',
  'Call Activity': '调用活动',
  
  // 事件定义
  'Timer Definition': '定时器定义',
  'Timer Definition Type': '定时器定义类型',
  'Timer Date': '定时器日期',
  'Timer Duration': '定时器持续时间',
  'Timer Cycle': '定时器周期',
  'Message Definition': '消息定义',
  'Message Name': '消息名称',
  'Signal Definition': '信号定义',
  'Signal Name': '信号名称',
  'Signal Scope': '信号范围',
  'Error Definition': '错误定义',
  'Error Name': '错误名称',
  'Error Code': '错误代码',
  'Error Code Variable': '错误代码变量',
  'Error Message Variable': '错误消息变量',
  'Escalation Definition': '升级定义',
  'Escalation Name': '升级名称',
  'Escalation Code': '升级代码',
  'Escalation Code Variable': '升级代码变量',
  'Conditional Definition': '条件定义',
  'Link Definition': '链接定义',
  'Link Name': '链接名称',
  'Compensation Definition': '补偿定义',
  'Wait for Completion': '等待完成',
  'Activity Ref': '活动引用',
  
  // 流程变量
  'Process Variables': '流程变量',
  'Variable': '变量',
  'Variables': '变量',
  'Variable Mapping': '变量映射',
  'Variable Assignment': '变量赋值',
  
  // 用户界面文本
  'Add': '添加',
  'Remove': '删除',
  'Edit': '编辑',
  'Save': '保存',
  'Cancel': '取消',
  'OK': '确定',
  'Yes': '是',
  'No': '否',
  'True': '真',
  'False': '假',
  'Enabled': '启用',
  'Disabled': '禁用',
  'Required': '必填',
  'Optional': '可选',
  'Default': '默认',
  'Custom': '自定义',
  'None': '无',
  'All': '全部',
  'Select': '选择',
  'Clear': '清除',
  'Reset': '重置',
  
  // 数据类型
  'String': '字符串',
  'Number': '数字',
  'Boolean': '布尔值',
  'Date': '日期',
  'Object': '对象',
  'Array': '数组',
  'List': '列表',
  'Map': '映射',
  
  // 绑定类型
  'latest': '最新',
  'deployment': '部署',
  'version': '版本',
  'versionTag': '版本标签',
  
  // 结果映射
  'singleEntry': '单条目',
  'singleResult': '单结果',
  'collectEntries': '收集条目',
  'resultList': '结果列表',
  
  // 作用域
  'global': '全局',
  'processInstance': '流程实例',
  
  // 常用按钮和操作
  'Create': '创建',
  'Update': '更新',
  'Delete': '删除',
  'Copy': '复制',
  'Paste': '粘贴',
  'Cut': '剪切',
  'Undo': '撤销',
  'Redo': '重做',
  'Zoom In': '放大',
  'Zoom Out': '缩小',
  'Fit Viewport': '适应视口',
  'Actual Size': '实际大小',
  
  // 验证消息
  'must not be empty': '不能为空',
  'must be a valid identifier': '必须是有效的标识符',
  'must be unique': '必须唯一',
  'invalid format': '格式无效',
  'required field': '必填字段',
  
  // 提示信息
  'Enter a value': '请输入值',
  'Select an option': '请选择选项',
  'Choose a file': '请选择文件',
  'Provide a name': '请提供名称',
  'Specify an expression': '请指定表达式',
  
  // 分组标题
  'Basic': '基础',
  'Advanced': '高级',
  'Configuration': '配置',
  'Settings': '设置',
  'Options': '选项',
  'Attributes': '属性',
  'Metadata': '元数据',
  'Security': '安全',
  'Performance': '性能',
  'Debugging': '调试'
}

/**
 * 属性面板翻译函数
 * @param {string} template - 要翻译的模板字符串
 * @param {Object} replacements - 替换参数
 * @returns {string} 翻译后的字符串
 */
export function translatePropertiesPanel(template, replacements) {
  replacements = replacements || {}

  // 获取翻译文本，如果没有找到则使用原文
  let translatedTemplate = propertiesPanelTranslations[template] || template

  // 替换模板中的占位符
  return translatedTemplate.replace(/{([^}]+)}/g, function(_, key) {
    return replacements[key] || '{' + key + '}'
  })
}

/**
 * 属性面板翻译模块配置
 */
export const propertiesPanelTranslateModule = {
  translate: ['value', translatePropertiesPanel]
}

export default propertiesPanelTranslateModule