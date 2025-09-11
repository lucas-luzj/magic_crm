/**
 * BPMN.js 中文翻译配置
 * 用于将BPMN流程设计器界面翻译为中文
 */

// 中文翻译映射表
const translations = {
  // 工具栏 - 基础元素
  'Activate the hand tool': '激活手型工具',
  'Activate the lasso tool': '激活套索工具',
  'Activate the create/remove space tool': '激活创建/删除空间工具',
  'Activate the global connect tool': '激活全局连接工具',
  
  // 工具栏 - 事件
  'Create StartEvent': '创建开始事件',
  'Create EndEvent': '创建结束事件',
  'Create IntermediateThrowEvent': '创建中间抛出事件',
  'Create IntermediateCatchEvent': '创建中间捕获事件',
  'Create BoundaryEvent': '创建边界事件',
  
  // 工具栏 - 活动
  'Create Task': '创建任务',
  'Create UserTask': '创建用户任务',
  'Create ServiceTask': '创建服务任务',
  'Create ScriptTask': '创建脚本任务',
  'Create BusinessRuleTask': '创建业务规则任务',
  'Create ReceiveTask': '创建接收任务',
  'Create SendTask': '创建发送任务',
  'Create ManualTask': '创建手工任务',
  'Create CallActivity': '创建调用活动',
  'Create SubProcess': '创建子流程',
  
  // 工具栏 - 网关
  'Create Gateway': '创建网关',
  'Create ExclusiveGateway': '创建排他网关',
  'Create InclusiveGateway': '创建包容网关',
  'Create ParallelGateway': '创建并行网关',
  'Create EventBasedGateway': '创建事件网关',
  'Create ComplexGateway': '创建复杂网关',
  
  // 工具栏 - 数据
  'Create DataObjectReference': '创建数据对象引用',
  'Create DataStoreReference': '创建数据存储引用',
  
  // 工具栏 - 参与者
  'Create Participant': '创建参与者',
  'Create expanded SubProcess': '创建展开的子流程',
  
  // 工具栏 - 连接
  'Create SequenceFlow': '创建顺序流',
  'Create MessageFlow': '创建消息流',
  'Create Association': '创建关联',
  'Create DataInputAssociation': '创建数据输入关联',
  'Create DataOutputAssociation': '创建数据输出关联',
  
  // 上下文菜单
  'Append Task': '追加任务',
  'Append UserTask': '追加用户任务',
  'Append ServiceTask': '追加服务任务',
  'Append ScriptTask': '追加脚本任务',
  'Append BusinessRuleTask': '追加业务规则任务',
  'Append ReceiveTask': '追加接收任务',
  'Append SendTask': '追加发送任务',
  'Append ManualTask': '追加手工任务',
  'Append CallActivity': '追加调用活动',
  'Append Gateway': '追加网关',
  'Append ExclusiveGateway': '追加排他网关',
  'Append InclusiveGateway': '追加包容网关',
  'Append ParallelGateway': '追加并行网关',
  'Append EventBasedGateway': '追加事件网关',
  'Append IntermediateThrowEvent': '追加中间抛出事件',
  'Append IntermediateCatchEvent': '追加中间捕获事件',
  'Append EndEvent': '追加结束事件',
  'Append TextAnnotation': '追加文本注释',
  
  // 操作
  'Remove': '删除',
  'Delete': '删除',
  'Connect using SequenceFlow': '使用顺序流连接',
  'Connect using MessageFlow': '使用消息流连接',
  'Connect using Association': '使用关联连接',
  'Connect using DataInputAssociation': '使用数据输入关联连接',
  'Connect using DataOutputAssociation': '使用数据输出关联连接',
  
  // 属性面板 - 通用
  'General': '通用',
  'Details': '详情',
  'Documentation': '文档',
  'Element Documentation': '元素文档',
  'Process Documentation': '流程文档',
  
  // 属性面板 - 标识
  'Id': '标识',
  'Name': '名称',
  'Process Id': '流程标识',
  'Process Name': '流程名称',
  'Executable': '可执行',
  'Version Tag': '版本标签',
  'History Time To Live': '历史生存时间',
  'Startable in Tasklist': '可在任务列表中启动',
  
  // 属性面板 - 表单
  'Forms': '表单',
  'Form Key': '表单键',
  'Form Fields': '表单字段',
  'Business Key': '业务键',
  'Form Data': '表单数据',
  
  // 属性面板 - 监听器
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
  
  // 属性面板 - 输入输出
  'Input/Output': '输入/输出',
  'Input Parameters': '输入参数',
  'Output Parameters': '输出参数',
  'Local Variable Name': '本地变量名',
  'Local Variable Assignment': '本地变量赋值',
  'Assignee': '受让人',
  'Candidate Users': '候选用户',
  'Candidate Groups': '候选组',
  'Due Date': '到期日期',
  'Follow Up Date': '跟进日期',
  'Priority': '优先级',
  
  // 属性面板 - 扩展
  'Extensions': '扩展',
  'Properties': '属性',
  'Property': '属性',
  'Value': '值',
  
  // 属性面板 - 条件
  'Condition': '条件',
  'Condition Type': '条件类型',
  'Condition Expression': '条件表达式',
  'Variable Name': '变量名',
  'Variable Event': '变量事件',
  'Variable Events': '变量事件',
  
  // 属性面板 - 多实例
  'Multi Instance': '多实例',
  'Loop Cardinality': '循环基数',
  'Collection': '集合',
  'Element Variable': '元素变量',
  'Completion Condition': '完成条件',
  'Sequential': '顺序',
  'Parallel': '并行',
  
  // 属性面板 - 异步
  'Asynchronous Continuations': '异步延续',
  'Asynchronous Before': '之前异步',
  'Asynchronous After': '之后异步',
  'Exclusive': '排他',
  'Job Priority': '作业优先级',
  'Retry Time Cycle': '重试时间周期',
  
  // 属性面板 - 外部任务
  'External Task Configuration': '外部任务配置',
  'Topic': '主题',
  'Task Priority': '任务优先级',
  
  // 属性面板 - 字段注入
  'Field Injections': '字段注入',
  'Field Name': '字段名',
  'Field Type': '字段类型',
  'String': '字符串',
  'Expression': '表达式',
  'String Value': '字符串值',
  'Expression Value': '表达式值',
  
  // 属性面板 - 连接器
  'Connector': '连接器',
  'Connector Id': '连接器标识',
  
  // 属性面板 - 实现
  'Implementation': '实现',
  'Implementation Type': '实现类型',
  'Class': '类',
  'Delegate Expression': '委托表达式',
  'External': '外部',
  'Connector': '连接器',
  'Decision Ref': '决策引用',
  'Decision Ref Binding': '决策引用绑定',
  'Decision Ref Version': '决策引用版本',
  'Decision Ref Version Tag': '决策引用版本标签',
  'Decision Ref Tenant Id': '决策引用租户标识',
  'Map Decision Result': '映射决策结果',
  'Result Variable': '结果变量',
  
  // 属性面板 - 调用活动
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
  
  // 属性面板 - 事件定义
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
  
  // 错误消息
  'Element is missing label/name': '元素缺少标签/名称',
  'Element is not connected': '元素未连接',
  'Element has invalid outgoing sequence flow': '元素有无效的输出顺序流',
  'Element has invalid incoming sequence flow': '元素有无效的输入顺序流',
  'Gateway has invalid outgoing sequence flow': '网关有无效的输出顺序流',
  'Gateway has invalid incoming sequence flow': '网关有无效的输入顺序流',
  'Sequence flow has invalid source': '顺序流有无效的源',
  'Sequence flow has invalid target': '顺序流有无效的目标',
  
  // 其他常用术语
  'Process': '流程',
  'Subprocess': '子流程',
  'Pool': '池',
  'Lane': '泳道',
  'Participant': '参与者',
  'Collaboration': '协作',
  'Message': '消息',
  'Signal': '信号',
  'Error': '错误',
  'Escalation': '升级',
  'Timer': '定时器',
  'Conditional': '条件',
  'Link': '链接',
  'Compensation': '补偿',
  'Terminate': '终止',
  'Cancel': '取消',
  'Multiple': '多重',
  'Parallel Multiple': '并行多重',
  'None': '无',
  'Untyped': '无类型',
  'Default': '默认',
  'Sequence Flow': '顺序流',
  'Message Flow': '消息流',
  'Association': '关联',
  'Data Association': '数据关联',
  'Text Annotation': '文本注释',
  'Group': '组',
  'Data Object': '数据对象',
  'Data Store': '数据存储',
  'Data Input': '数据输入',
  'Data Output': '数据输出',
  'Start Event': '开始事件',
  'End Event': '结束事件',
  'Intermediate Event': '中间事件',
  'Boundary Event': '边界事件',
  'Task': '任务',
  'User Task': '用户任务',
  'Service Task': '服务任务',
  'Script Task': '脚本任务',
  'Business Rule Task': '业务规则任务',
  'Receive Task': '接收任务',
  'Send Task': '发送任务',
  'Manual Task': '手工任务',
  'Call Activity': '调用活动',
  'Gateway': '网关',
  'Exclusive Gateway': '排他网关',
  'Inclusive Gateway': '包容网关',
  'Parallel Gateway': '并行网关',
  'Event Based Gateway': '事件网关',
  'Complex Gateway': '复杂网关'
}

/**
 * 自定义翻译函数
 * @param {string} template - 要翻译的模板字符串
 * @param {Object} replacements - 替换参数
 * @returns {string} 翻译后的字符串
 */
export function customTranslate(template, replacements) {
  replacements = replacements || {}

  // 获取翻译文本，如果没有找到则使用原文
  let translatedTemplate = translations[template] || template

  // 替换模板中的占位符
  return translatedTemplate.replace(/{([^}]+)}/g, function(_, key) {
    return replacements[key] || '{' + key + '}'
  })
}

/**
 * 翻译模块配置
 */
export const translateModule = {
  translate: ['value', customTranslate]
}

export default translateModule