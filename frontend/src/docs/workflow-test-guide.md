# 工作流测试指南

## 问题解决方案：流程变量绑定

### 问题描述
启动请假流程时出现错误：`Unknown property used in expression: ${employee}`

这是因为工作流引擎在执行流程时找不到对应的变量值。

### 解决方案

我们已经创建了完整的变量解析系统来解决这个问题：

#### 1. ProcessVariableService（流程变量服务）
- **位置**: `frontend/src/services/processVariableService.js`
- **功能**: 自动解析和设置流程变量
- **支持的变量**:
  - `${employee}` - 申请人用户名
  - `${manager}` - 部门经理用户名  
  - `${hr}` - HR用户名
  - `${days}` - 请假天数
  - 其他业务变量

#### 2. 请假表单模板
- **位置**: `frontend/src/data/leaveFormTemplate.js`
- **功能**: 为请假流程提供专门的表单配置
- **特点**: 
  - 自动填充用户信息
  - 支持日期计算
  - 表单验证

#### 3. 更新的流程启动逻辑
- **位置**: `frontend/src/views/workflow/ProcessStart.vue`
- **功能**: 集成变量解析服务
- **流程**:
  1. 验证表单数据
  2. 获取当前用户信息
  3. 解析流程变量
  4. 启动流程实例

## 测试步骤

### 前提条件
1. 确保已登录系统
2. 确保请假流程已部署到Flowable引擎
3. 确保用户store中有用户信息

### 测试流程启动

#### 步骤1：访问流程启动页面
```
访问URL: /workflow/process-start/{processDefinitionId}
```

#### 步骤2：填写请假表单
系统会自动显示请假表单，包含以下字段：
- 申请人姓名（自动填充）
- 员工工号（自动填充）
- 请假类型（下拉选择）
- 请假天数（数字输入）
- 开始日期（日期选择）
- 结束日期（日期选择）
- 请假原因（文本域）
- 联系电话（可选）
- 紧急联系人（可选）
- 工作交接（可选）

#### 步骤3：提交表单
点击"启动流程"按钮，系统会：
1. 验证表单数据
2. 解析流程变量
3. 调用后端API启动流程

### 变量解析示例

假设当前用户信息：
```javascript
{
  id: 1,
  username: "zhangsan",
  name: "张三",
  deptId: 10
}
```

表单数据：
```javascript
{
  leaveType: "annual",
  days: 3,
  startDate: "2024-01-15",
  endDate: "2024-01-17",
  reason: "年假休息"
}
```

解析后的流程变量：
```javascript
{
  // 用户相关变量
  employee: "zhangsan",
  employeeName: "张三",
  employeeId: 1,
  applicant: "zhangsan",
  applicantName: "张三",
  
  // 审批人变量
  manager: "lisi", // 从部门信息中获取
  managerName: "李四",
  hr: "hr",
  hrName: "HR部门",
  
  // 业务变量
  leaveType: "annual",
  days: 3,
  startDate: "2024-01-15",
  endDate: "2024-01-17",
  reason: "年假休息",
  
  // 系统变量
  startTime: "2024-01-15T09:00:00.000Z",
  businessKey: "leave_1_1705392000000"
}
```

## 故障排除

### 1. 变量未找到错误
**错误**: `Unknown property used in expression: ${variable}`

**解决方案**:
- 检查ProcessVariableService是否正确解析变量
- 确保用户信息完整
- 检查部门和角色配置

### 2. 用户分配错误
**错误**: `Could not find user with id: ${manager}`

**解决方案**:
- 确保部门经理用户存在
- 检查用户角色配置
- 使用默认用户作为后备方案

### 3. 表单验证失败
**错误**: 表单验证不通过

**解决方案**:
- 检查必填字段
- 验证日期格式
- 确保数值范围正确

## API接口说明

### 启动流程接口
```javascript
POST /api/workflow/process-instances/start
{
  "processDefinitionId": "leaveProcess:1:xxx",
  "variables": {
    "employee": "zhangsan",
    "manager": "lisi",
    "hr": "hr",
    "days": 3,
    // ... 其他变量
  },
  "businessKey": "leave_1_1705392000000"
}
```

### 响应格式
```javascript
{
  "success": true,
  "data": {
    "id": "process-instance-id",
    "processDefinitionId": "leaveProcess:1:xxx",
    "businessKey": "leave_1_1705392000000",
    "startTime": "2024-01-15T09:00:00.000Z"
  },
  "message": "流程启动成功"
}
```

## 下一步开发

1. **完善用户和部门管理**
   - 确保用户信息完整
   - 配置部门经理关系
   - 设置HR角色用户

2. **优化变量解析**
   - 支持更多流程类型
   - 增加变量验证规则
   - 提供变量配置界面

3. **增强错误处理**
   - 更详细的错误信息
   - 自动修复机制
   - 日志记录

4. **测试和监控**
   - 单元测试
   - 集成测试
   - 性能监控

## 常见问题

**Q: 如何添加新的流程变量？**
A: 在ProcessVariableService中的resolveProcessVariables方法中添加变量解析逻辑。

**Q: 如何为新流程类型创建表单模板？**
A: 参考leaveFormTemplate.js创建新的表单模板，并在FormConfigService中添加识别逻辑。

**Q: 如何处理复杂的用户分配逻辑？**
A: 在ProcessVariableService中扩展getDepartmentManager和getHRUser方法。

**Q: 如何调试变量解析过程？**
A: 查看浏览器控制台日志，ProcessVariableService会输出详细的解析过程。