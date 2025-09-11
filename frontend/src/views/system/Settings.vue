<template>
  <div class="system-settings">
    <div class="page-header">
      <h2>系统设置</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleSaveAll">
          <el-icon><Check /></el-icon>
          保存所有设置
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 设置分组标签页 -->
    <el-tabs v-model="activeGroup" @tab-change="handleGroupChange">
      <el-tab-pane
        v-for="group in settingGroups"
        :key="group"
        :label="getGroupLabel(group)"
        :name="group"
      >
        <div class="settings-content">
          <el-form :model="groupSettings" label-width="200px" class="settings-form">
            <div
              v-for="setting in groupSettings"
              :key="setting.id"
              class="setting-item"
            >
              <el-form-item :label="setting.settingName">
                <div class="setting-control">
                  <!-- 字符串类型 -->
                  <el-input
                    v-if="setting.settingType === 'STRING'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    clearable
                  />
                  
                  <!-- 数字类型 -->
                  <el-input-number
                    v-else-if="setting.settingType === 'NUMBER'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    controls-position="right"
                    style="width: 100%"
                  />
                  
                  <!-- 布尔类型 -->
                  <el-switch
                    v-else-if="setting.settingType === 'BOOLEAN'"
                    v-model="setting.settingValue"
                    :disabled="!setting.isEditable"
                  />
                  
                  <!-- 邮箱类型 -->
                  <el-input
                    v-else-if="setting.settingType === 'EMAIL'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    type="email"
                    clearable
                  />
                  
                  <!-- URL类型 -->
                  <el-input
                    v-else-if="setting.settingType === 'URL'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    clearable
                  />
                  
                  <!-- 密码类型 -->
                  <el-input
                    v-else-if="setting.settingType === 'PASSWORD'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    type="password"
                    show-password
                    clearable
                  />
                  
                  <!-- 长文本类型 -->
                  <el-input
                    v-else-if="setting.settingType === 'TEXT'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    type="textarea"
                    :rows="3"
                    clearable
                  />
                  
                  <!-- JSON类型 -->
                  <el-input
                    v-else-if="setting.settingType === 'JSON'"
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    type="textarea"
                    :rows="4"
                    clearable
                  />
                  
                  <!-- 默认字符串类型 -->
                  <el-input
                    v-else
                    v-model="setting.settingValue"
                    :placeholder="setting.defaultValue"
                    :disabled="!setting.isEditable"
                    clearable
                  />
                  
                  <!-- 操作按钮 -->
                  <div class="setting-actions">
                    <el-button
                      v-if="setting.isEditable"
                      type="text"
                      size="small"
                      @click="handleReset(setting)"
                    >
                      重置
                    </el-button>
                    <el-button
                      v-if="setting.isEditable"
                      type="text"
                      size="small"
                      @click="handleSave(setting)"
                    >
                      保存
                    </el-button>
                    <el-button
                      v-if="isTestableSetting(setting)"
                      type="text"
                      size="small"
                      @click="handleTest(setting)"
                    >
                      测试
                    </el-button>
                  </div>
                </div>
                
                <!-- 设置描述 -->
                <div v-if="setting.description" class="setting-description">
                  {{ setting.description }}
                </div>
              </el-form-item>
            </div>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 测试配置对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      :title="testDialogTitle"
      width="500px"
    >
      <el-form :model="testForm" label-width="100px">
        <el-form-item v-if="testDialogTitle.includes('邮件')" label="测试邮箱">
          <el-input v-model="testForm.email" placeholder="请输入测试邮箱" />
        </el-form-item>
        <el-form-item v-if="testDialogTitle.includes('短信')" label="测试手机号">
          <el-input v-model="testForm.phone" placeholder="请输入测试手机号" />
        </el-form-item>
        <el-form-item v-if="testDialogTitle.includes('微信') || testDialogTitle.includes('钉钉') || testDialogTitle.includes('推送')" label="用户ID">
          <el-input v-model="testForm.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item v-if="testDialogTitle.includes('推送')" label="推送标题">
          <el-input v-model="testForm.title" placeholder="请输入推送标题" />
        </el-form-item>
        <el-form-item label="测试内容">
          <el-input
            v-model="testForm.content"
            type="textarea"
            :rows="3"
            placeholder="请输入测试内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="testDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleTestConfirm">发送测试</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Refresh } from '@element-plus/icons-vue'
import { systemSettingApi } from '@/api/systemSetting'

// 响应式数据
const loading = ref(false)
const activeGroup = ref('email')
const settingGroups = ref([])
const groupSettings = ref([])
const testDialogVisible = ref(false)
const testDialogTitle = ref('')
const testForm = reactive({
  email: '',
  phone: '',
  userId: '',
  title: '',
  content: ''
})

// 分组标签映射
const groupLabels = {
  email: '邮件配置',
  sms: '短信配置',
  wechat: '微信配置',
  dingtalk: '钉钉配置',
  push: '推送配置',
  system: '系统配置',
  security: '安全配置',
  notification: '通知配置',
  file: '文件配置'
}

// 计算属性
const getGroupLabel = (group) => {
  return groupLabels[group] || group
}

// 方法
const loadSettingGroups = async () => {
  try {
    const response = await systemSettingApi.getAllSettingGroups()
    if (response.success) {
      settingGroups.value = response.data
      if (settingGroups.value.length > 0) {
        activeGroup.value = settingGroups.value[0]
        await loadGroupSettings(activeGroup.value)
      }
    }
  } catch (error) {
    ElMessage.error('加载设置分组失败')
  }
}

const loadGroupSettings = async (group) => {
  loading.value = true
  try {
    const response = await systemSettingApi.getSettingsByGroup(group)
    if (response.success) {
      groupSettings.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载设置失败')
  } finally {
    loading.value = false
  }
}

const handleGroupChange = (group) => {
  loadGroupSettings(group)
}

const handleSave = async (setting) => {
  try {
    const response = await systemSettingApi.setSettingValue(setting.settingKey, setting.settingValue)
    if (response.success) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.error || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleSaveAll = async () => {
  try {
    const settings = {}
    groupSettings.value.forEach(setting => {
      if (setting.isEditable) {
        settings[setting.settingKey] = setting.settingValue
      }
    })
    
    const response = await systemSettingApi.setSettingValues(settings)
    if (response.success) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.error || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleReset = async (setting) => {
  try {
    await ElMessageBox.confirm('确定要重置为默认值吗？', '确认重置', {
      type: 'warning'
    })
    
    const response = await systemSettingApi.resetToDefault(setting.settingKey)
    if (response.success) {
      setting.settingValue = response.data.settingValue
      ElMessage.success('重置成功')
    } else {
      ElMessage.error(response.error || '重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置失败')
    }
  }
}

const handleRefresh = () => {
  loadGroupSettings(activeGroup.value)
}

const handleTestEmail = () => {
  testDialogTitle.value = '测试邮件配置'
  testForm.email = ''
  testForm.content = '这是一封测试邮件，用于验证邮件配置是否正确。'
  testDialogVisible.value = true
}

const handleTestSms = () => {
  testDialogTitle.value = '测试短信配置'
  testForm.email = ''
  testForm.content = '这是一条测试短信，用于验证短信配置是否正确。'
  testDialogVisible.value = true
}

const handleTestConfirm = async () => {
  try {
    let response
    if (testDialogTitle.value.includes('邮件')) {
      response = await systemSettingApi.testEmailConfig(testForm)
    } else if (testDialogTitle.value.includes('短信')) {
      response = await systemSettingApi.testSmsConfig(testForm)
    } else if (testDialogTitle.value.includes('微信')) {
      response = await systemSettingApi.testWechatConfig(testForm)
    } else if (testDialogTitle.value.includes('钉钉')) {
      response = await systemSettingApi.testDingtalkConfig(testForm)
    } else if (testDialogTitle.value.includes('推送')) {
      response = await systemSettingApi.testPushConfig(testForm)
    }
    
    if (response.success) {
      ElMessage.success('测试发送成功')
      testDialogVisible.value = false
    } else {
      ElMessage.error(response.error || '测试发送失败')
    }
  } catch (error) {
    ElMessage.error('测试发送失败')
  }
}

const isTestableSetting = (setting) => {
  const testableKeys = [
    'email.smtp.host', 'email.smtp.username', 'email.smtp.password',
    'sms.aliyun.accessKeyId', 'sms.aliyun.accessKeySecret',
    'wechat.corpId', 'wechat.corpSecret',
    'dingtalk.appKey', 'dingtalk.appSecret', 'dingtalk.webhookUrl',
    'push.jpush.appKey', 'push.jpush.masterSecret'
  ]
  return testableKeys.includes(setting.settingKey)
}

const handleTest = (setting) => {
  if (setting.settingKey.startsWith('email.')) {
    testDialogTitle.value = '测试邮件配置'
    testForm.email = ''
    testForm.content = '这是一封测试邮件，用于验证邮件配置是否正确。'
  } else if (setting.settingKey.startsWith('sms.')) {
    testDialogTitle.value = '测试短信配置'
    testForm.phone = ''
    testForm.content = '这是一条测试短信，用于验证短信配置是否正确。'
  } else if (setting.settingKey.startsWith('wechat.')) {
    testDialogTitle.value = '测试微信配置'
    testForm.userId = ''
    testForm.content = '这是一条测试微信消息，用于验证微信配置是否正确。'
  } else if (setting.settingKey.startsWith('dingtalk.')) {
    testDialogTitle.value = '测试钉钉配置'
    testForm.userId = ''
    testForm.content = '这是一条测试钉钉消息，用于验证钉钉配置是否正确。'
  } else if (setting.settingKey.startsWith('push.')) {
    testDialogTitle.value = '测试推送配置'
    testForm.userId = ''
    testForm.title = '测试推送'
    testForm.content = '这是一条测试推送消息，用于验证推送配置是否正确。'
  }
  testDialogVisible.value = true
}

// 生命周期
onMounted(() => {
  loadSettingGroups()
})
</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.settings-content {
  background: white;
  border-radius: 4px;
  padding: 20px;
}

.settings-form {
  max-width: 800px;
}

.setting-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  background: #fafafa;
}

.setting-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.setting-control .el-input,
.setting-control .el-input-number,
.setting-control .el-textarea {
  flex: 1;
}

.setting-actions {
  display: flex;
  gap: 5px;
}

.setting-description {
  margin-top: 5px;
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.dialog-footer {
  text-align: right;
}
</style>
