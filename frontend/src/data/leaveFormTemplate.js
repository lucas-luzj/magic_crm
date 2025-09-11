/**
 * 请假流程表单模板
 * 用于测试请假流程的动态表单功能
 */
export const leaveFormTemplate = {
  formKey: 'start',
  formName: '请假申请表',
  components: [
    {
      type: 'input',
      field: 'employeeName',
      label: '申请人姓名',
      required: true,
      span: 12,
      placeholder: '请输入申请人姓名',
      disabled: true, // 自动填充，不允许修改
      defaultValue: ''
    },
    {
      type: 'input',
      field: 'employeeId',
      label: '员工工号',
      required: true,
      span: 12,
      placeholder: '请输入员工工号',
      disabled: true, // 自动填充，不允许修改
      defaultValue: ''
    },
    {
      type: 'select',
      field: 'leaveType',
      label: '请假类型',
      required: true,
      span: 12,
      placeholder: '请选择请假类型',
      options: [
        { label: '年假', value: 'annual' },
        { label: '病假', value: 'sick' },
        { label: '事假', value: 'personal' },
        { label: '调休', value: 'compensatory' },
        { label: '婚假', value: 'marriage' },
        { label: '产假', value: 'maternity' },
        { label: '丧假', value: 'bereavement' }
      ]
    },
    {
      type: 'input-number',
      field: 'days',
      label: '请假天数',
      required: true,
      span: 12,
      placeholder: '请输入请假天数',
      min: 0.5,
      max: 365,
      step: 0.5,
      precision: 1,
      defaultValue: 1
    },
    {
      type: 'date-picker',
      field: 'startDate',
      label: '开始日期',
      required: true,
      span: 12,
      placeholder: '请选择开始日期',
      type: 'date',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD'
    },
    {
      type: 'date-picker',
      field: 'endDate',
      label: '结束日期',
      required: true,
      span: 12,
      placeholder: '请选择结束日期',
      type: 'date',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD'
    },
    {
      type: 'textarea',
      field: 'reason',
      label: '请假原因',
      required: true,
      span: 24,
      placeholder: '请详细说明请假原因',
      rows: 4,
      maxlength: 500,
      showWordLimit: true
    },
    {
      type: 'input',
      field: 'contactPhone',
      label: '联系电话',
      required: false,
      span: 12,
      placeholder: '请假期间联系电话'
    },
    {
      type: 'input',
      field: 'emergencyContact',
      label: '紧急联系人',
      required: false,
      span: 12,
      placeholder: '紧急联系人姓名'
    },
    {
      type: 'textarea',
      field: 'workHandover',
      label: '工作交接',
      required: false,
      span: 24,
      placeholder: '请说明工作交接安排（可选）',
      rows: 3,
      maxlength: 300,
      showWordLimit: true
    }
  ],
  labelPosition: 'left',
  labelWidth: 120,
  size: 'default',
  // 计算变量：根据开始和结束日期自动计算天数
  computedVariables: [
    {
      name: 'calculatedDays',
      expression: 'model.startDate && model.endDate ? Math.ceil((new Date(model.endDate) - new Date(model.startDate)) / (1000 * 60 * 60 * 24)) + 1 : 1'
    }
  ],
  // 表达式：自动更新天数字段
  expressions: [
    {
      target: 'days',
      value: 'computed.calculatedDays'
    }
  ]
}

/**
 * 获取请假表单模板
 * @param {Object} currentUser - 当前用户信息
 * @returns {Object} 表单配置
 */
export function getLeaveFormTemplate(currentUser = {}) {
  const template = JSON.parse(JSON.stringify(leaveFormTemplate))
  
  // 自动填充用户信息
  const employeeNameComponent = template.components.find(c => c.field === 'employeeName')
  if (employeeNameComponent && currentUser.name) {
    employeeNameComponent.defaultValue = currentUser.name
  }
  
  const employeeIdComponent = template.components.find(c => c.field === 'employeeId')
  if (employeeIdComponent && currentUser.id) {
    employeeIdComponent.defaultValue = currentUser.id.toString()
  }
  
  return template
}

export default leaveFormTemplate