/**
 * 工作流变量处理工具函数
 */

/**
 * 从表达式中提取所有变量名
 * @param {string} expression - 条件表达式
 * @returns {string[]} 变量名数组
 */
export const extractVariablesFromExpression = (expression) => {
  if (!expression) return []
  
  // 匹配 ${...} 格式的整个表达式块
  const expressionBlockRegex = /\$\{([^}]+)\}/g
  const expressionBlocks = []
  let match
  
  // 提取所有 ${...} 表达式块
  while ((match = expressionBlockRegex.exec(expression)) !== null) {
    expressionBlocks.push(match[1])
  }
  
  if (expressionBlocks.length === 0) return []
  
  const allVariables = []
  
  // 处理每个表达式块，提取其中的变量名
  expressionBlocks.forEach(block => {
    // 先移除字符串字面量（单引号、双引号、反引号中的内容）
    const blockWithoutStrings = block.replace(/['"`](?:[^\\]|\\.)*?['"`]/g, '')
    
    // 匹配变量名（中文、英文、数字、下划线，不能以数字开头）
    const variableRegex = /([\u4e00-\u9fa5a-zA-Z_][\u4e00-\u9fa5a-zA-Z0-9_]*)/g
    let variableMatch
    
    // 提取所有变量名
    while ((variableMatch = variableRegex.exec(blockWithoutStrings)) !== null) {
      const variableName = variableMatch[1]
      
      // 排除JavaScript关键字和操作符
      if (!isJavaScriptKeyword(variableName) && !isOperator(variableName)) {
        allVariables.push(variableName)
      }
    }
  })
  
  return [...new Set(allVariables)]
}

/**
 * 检查是否为JavaScript关键字
 * @param {string} word - 要检查的单词
 * @returns {boolean} 是否为关键字
 */
const isJavaScriptKeyword = (word) => {
  const keywords = [
    'true', 'false', 'null', 'undefined', 'this', 'super', 'new',
    'function', 'return', 'if', 'else', 'for', 'while', 'do', 'switch',
    'case', 'default', 'break', 'continue', 'try', 'catch', 'finally',
    'throw', 'var', 'let', 'const', 'class', 'extends', 'import', 'export',
    'void', 'typeof', 'instanceof', 'in', 'delete', 'yield', 'await', 'async'
  ]
  return keywords.includes(word)
}

/**
 * 检查是否为操作符
 * @param {string} word - 要检查的单词
 * @returns {boolean} 是否为操作符
 */
const isOperator = (word) => {
  const operators = [
    '==', '===', '!=', '!==', '<', '>', '<=', '>=', '&&', '||', '!',
    '+', '-', '*', '/', '%', '=', '&', '|', '^', '~', '<<', '>>', '>>>'
  ]
  return operators.includes(word)
}

/**
 * 验证变量名合法性
 * @param {string} name - 变量名
 * @returns {string|null} 错误信息或null
 */
export const validateVariableName = (name) => {
  if (!name || name.trim() === '') {
    return '变量名不能为空'
  }
  
  // 支持中文、英文、数字、下划线，但不能以数字开头，不能包含空格和特殊符号
  const validNameRegex = /^[\u4e00-\u9fa5a-zA-Z_][\u4e00-\u9fa5a-zA-Z0-9_]*$/
  if (!validNameRegex.test(name)) {
    return '变量名只能包含中文、英文、数字、下划线，且不能以数字开头，不能包含空格或特殊符号'
  }
  
  return null
}

/**
 * 转义正则表达式特殊字符
 * @param {string} string - 原始字符串
 * @returns {string} 转义后的字符串
 */
export const escapeRegExp = (string) => {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

/**
 * 在整个流程中更新变量名引用
 * @param {Object} bpmnModeler - BPMN模型器实例
 * @param {string} oldName - 旧变量名
 * @param {string} newName - 新变量名
 * @returns {number} 更新的元素数量
 */
export const updateVariableReferences = (bpmnModeler, oldName, newName) => {
  if (!bpmnModeler || oldName === newName) return 0
  
  try {
    const elementRegistry = bpmnModeler.get('elementRegistry')
    const modeling = bpmnModeler.get('modeling')
    const moddle = bpmnModeler.get('moddle')
    const elements = elementRegistry.getAll()
    
    let updatedCount = 0
    
    elements.forEach(element => {
      const businessObject = element.businessObject
      if (!businessObject) return
      
      let needsUpdate = false
      const updates = {}
      
      // 更新条件表达式中的变量引用
      if (businessObject.conditionExpression?.body) {
        const oldExpression = businessObject.conditionExpression.body
        const newExpression = replaceAllVariableReferences(oldExpression, oldName, newName)
        
        if (oldExpression !== newExpression) {
          updates.conditionExpression = moddle.create('bpmn:FormalExpression', {
            body: newExpression
          })
          needsUpdate = true
        }
      }
      
      // 更新任务分配中的变量引用
      if (businessObject.assignee) {
        const oldAssignee = businessObject.assignee
        const newAssignee = replaceAllVariableReferences(oldAssignee, oldName, newName)
        
        if (oldAssignee !== newAssignee) {
          updates.assignee = newAssignee
          needsUpdate = true
        }
      }
      
      // 更新候选用户中的变量引用
      if (businessObject.candidateUsers) {
        const oldCandidateUsers = businessObject.candidateUsers
        const newCandidateUsers = replaceAllVariableReferences(oldCandidateUsers, oldName, newName)
        
        if (oldCandidateUsers !== newCandidateUsers) {
          updates.candidateUsers = newCandidateUsers
          needsUpdate = true
        }
      }
      
      // 更新候选组中的变量引用
      if (businessObject.candidateGroups) {
        const oldCandidateGroups = businessObject.candidateGroups
        const newCandidateGroups = replaceAllVariableReferences(oldCandidateGroups, oldName, newName)
        
        if (oldCandidateGroups !== newCandidateGroups) {
          updates.candidateGroups = newCandidateGroups
          needsUpdate = true
        }
      }
      
      // 更新其他可能包含变量的字段
      if (businessObject.documentation) {
        const oldDocumentation = businessObject.documentation
        const newDocumentation = replaceAllVariableReferences(oldDocumentation, oldName, newName)
        
        if (oldDocumentation !== newDocumentation) {
          updates.documentation = newDocumentation
          needsUpdate = true
        }
      }
      
      if (needsUpdate) {
        modeling.updateProperties(element, updates)
        updatedCount++
      }
    })
    
    return updatedCount
    
  } catch (error) {
    console.error('更新变量引用失败:', error)
    return 0
  }
}

/**
 * 替换字符串中所有变量引用
 * @param {string} content - 原始内容
 * @param {string} oldName - 旧变量名
 * @param {string} newName - 新变量名
 * @returns {string} 替换后的内容
 */
const replaceAllVariableReferences = (content, oldName, newName) => {
  if (!content || typeof content !== 'string') return content
  
  const escapedOldName = escapeRegExp(oldName)
  
  // 替换 ${oldName} 格式的完整变量引用
  let newContent = content.replace(
    new RegExp(`\\$\\{\\s*${escapedOldName}\\s*\\}`, 'g'),
    `\${${newName}}`
  )
  
  // 替换 ${oldName op...} 格式的变量引用（变量名在操作符前）
  newContent = newContent.replace(
    new RegExp(`\\$\\{\\s*${escapedOldName}\\s*([<>=!&|+\\-*/\\s])`, 'g'),
    `\${${newName}$1`
  )
  
  // 替换 ${... op oldName} 格式的变量引用（变量名在操作符后）
  newContent = newContent.replace(
    new RegExp(`\\$\\{\\s*([<>=!&|+\\-*/\\s])\\s*${escapedOldName}\\s*\\}`, 'g'),
    `\${$1${newName}}`
  )
  
  // 替换 ${... op oldName op...} 格式的变量引用（变量名在操作符中间）
  newContent = newContent.replace(
    new RegExp(`\\$\\{\\s*([<>=!&|+\\-*/\\s])\\s*${escapedOldName}\\s*([<>=!&|+\\-*/\\s])`, 'g'),
    `\${$1${newName}$2`
  )
  
  return newContent
}

/**
 * 检查变量名是否重复
 * @param {Array} variables - 变量数组
 * @param {string} name - 要检查的变量名
 * @param {number} currentIndex - 当前变量的索引（编辑时使用）
 * @returns {boolean} 是否重复
 */
export const isVariableNameDuplicate = (variables, name, currentIndex = -1) => {
  return variables.some((variable, index) => 
    variable.name === name && index !== currentIndex
  )
}