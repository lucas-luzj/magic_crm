<template>
  <div class="notification-settings">
    <div class="page-header">
      <h2>通知设置</h2>
      <div class="header-actions">
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置为默认
        </el-button>
        <el-button type="primary" @click="handleSave">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 渠道设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Bell /></el-icon>
              <span>通知渠道设置</span>
            </div>
          </template>
          
          <el-form :model="settings" label-width="120px">
            <el-form-item label="站内信通知">
              <el-switch v-model="settings.enableInSite" />
              <span class="form-tip">接收系统内的消息通知</span>
            </el-form-item>
            
            <el-form-item label="邮件通知">
              <el-switch v-model="settings.enableEmail" />
              <span class="form-tip">通过邮件接收重要通知</span>
            </el-form-item>
            
            <el-form-item label="短信通知">
              <el-switch v-model="settings.enableSms" />
              <span class="form-tip">通过短信接收紧急通知</span>
            </el-form-item>
            
            <el-form-item label="微信通知">
              <el-switch v-model="settings.enableWechat" />
              <span class="form-tip">通过微信接收通知</span>
            </el-form-item>
            
            <el-form-item label="钉钉通知">
              <el-switch v-model="settings.enableDingtalk" />
              <span class="form-tip">通过钉钉接收通知</span>
            </el-form-item>
            
            <el-form-item label="推送通知">
              <el-switch v-model="settings.enablePush" />
              <span class="form-tip">接收浏览器推送通知</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 时间设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Clock /></el-icon>
              <span>时间设置</span>
            </div>
          </template>
          
          <el-form :model="settings" label-width="120px">
            <el-form-item label="邮件通知时间">
              <el-time-picker
                v-model="emailTimeRange"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleEmailTimeChange"
              />
            </el-form-item>
            
            <el-form-item label="短信通知时间">
              <el-time-picker
                v-model="smsTimeRange"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleSmsTimeChange"
              />
            </el-form-item>
            
            <el-form-item label="免打扰时间">
              <el-time-picker
                v-model="quietTimeRange"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleQuietTimeChange"
              />
            </el-form-item>
            
            <el-form-item label="周末通知">
              <el-switch v-model="settings.enableWeekendNotifications" />
              <span class="form-tip">是否在周末接收通知</span>
            </el-form-item>
            
            <el-form-item label="节假日通知">
              <el-switch v-model="settings.enableHolidayNotifications" />
              <span class="form-tip">是否在节假日接收通知</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 通知类型设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>通知类型设置</span>
            </div>
          </template>
          
          <el-form :model="settings" label-width="120px">
            <el-form-item label="工作流通知">
              <el-switch v-model="workflowNotifications.enabled" />
              <el-button
                v-if="workflowNotifications.enabled"
                type="text"
                size="small"
                @click="showWorkflowSettings = true"
              >
                详细设置
              </el-button>
            </el-form-item>
            
            <el-form-item label="任务通知">
              <el-switch v-model="taskNotifications.enabled" />
              <el-button
                v-if="taskNotifications.enabled"
                type="text"
                size="small"
                @click="showTaskSettings = true"
              >
                详细设置
              </el-button>
            </el-form-item>
            
            <el-form-item label="系统通知">
              <el-switch v-model="systemNotifications.enabled" />
              <el-button
                v-if="systemNotifications.enabled"
                type="text"
                size="small"
                @click="showSystemSettings = true"
              >
                详细设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 其他设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>其他设置</span>
            </div>
          </template>
          
          <el-form :model="settings" label-width="120px">
            <el-form-item label="频率限制">
              <el-input-number
                v-model="settings.frequencyLimitMinutes"
                :min="1"
                :max="60"
                controls-position="right"
              />
              <span class="form-tip">分钟，相同类型通知的最小间隔时间</span>
            </el-form-item>
            
            <el-form-item label="通知预览">
              <el-button @click="handlePreview">
                <el-icon><View /></el-icon>
                预览通知
              </el-button>
            </el-form-item>
            
            <el-form-item label="测试通知">
              <el-button @click="handleTest">
                <el-icon><Bell /></el-icon>
                发送测试通知
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 工作流通知详细设置对话框 -->
    <el-dialog
      v-model="showWorkflowSettings"
      title="工作流通知设置"
      width="600px"
    >
      <el-form :model="workflowNotifications" label-width="120px">
        <el-form-item label="流程启动">
          <el-switch v-model="workflowNotifications.processStarted" />
        </el-form-item>
        <el-form-item label="流程完成">
          <el-switch v-model="workflowNotifications.processCompleted" />
        </el-form-item>
        <el-form-item label="流程暂停">
          <el-switch v-model="workflowNotifications.processSuspended" />
        </el-form-item>
        <el-form-item label="任务分配">
          <el-switch v-model="workflowNotifications.taskAssigned" />
        </el-form-item>
        <el-form-item label="任务完成">
          <el-switch v-model="workflowNotifications.taskCompleted" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWorkflowSettings = false">取消</el-button>
        <el-button type="primary" @click="handleSaveWorkflowSettings">保存</el-button>
      </template>
    </el-dialog>

    <!-- 任务通知详细设置对话框 -->
    <el-dialog
      v-model="showTaskSettings"
      title="任务通知设置"
      width="600px"
    >
      <el-form :model="taskNotifications" label-width="120px">
        <el-form-item label="新任务提醒">
          <el-switch v-model="taskNotifications.newTask" />
        </el-form-item>
        <el-form-item label="任务超时提醒">
          <el-switch v-model="taskNotifications.taskOverdue" />
        </el-form-item>
        <el-form-item label="任务即将到期">
          <el-switch v-model="taskNotifications.taskDueSoon" />
        </el-form-item>
        <el-form-item label="任务完成通知">
          <el-switch v-model="taskNotifications.taskCompleted" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTaskSettings = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTaskSettings">保存</el-button>
      </template>
    </el-dialog>

    <!-- 系统通知详细设置对话框 -->
    <el-dialog
      v-model="showSystemSettings"
      title="系统通知设置"
      width="600px"
    >
      <el-form :model="systemNotifications" label-width="120px">
        <el-form-item label="系统维护">
          <el-switch v-model="systemNotifications.maintenance" />
        </el-form-item>
        <el-form-item label="系统升级">
          <el-switch v-model="systemNotifications.upgrade" />
        </el-form-item>
        <el-form-item label="安全提醒">
          <el-switch v-model="systemNotifications.security" />
        </el-form-item>
        <el-form-item label="功能更新">
          <el-switch v-model="systemNotifications.featureUpdate" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSystemSettings = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSystemSettings">保存</el-button>
      </template>
    </el-dialog>

    <!-- 通知预览对话框 -->
    <el-dialog
      v-model="showPreview"
      title="通知预览"
      width="500px"
    >
      <div class="notification-preview">
        <div class="preview-item">
          <strong>站内信通知：</strong>
          <div class="preview-content">
            <h4>新任务分配</h4>
            <p>您有一个新任务需要处理：测试任务</p>
            <small>2024-01-01 10:00:00</small>
          </div>
        </div>
        <div class="preview-item">
          <strong>邮件通知：</strong>
          <div class="preview-content">
            <h4>【任务通知】测试任务</h4>
            <p>尊敬的 张三，您有一个新任务需要处理...</p>
            <small>2024-01-01 10:00:00</small>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Check, Bell, Clock, Document, Setting, View } from '@element-plus/icons-vue'
import { notificationSettingsApi } from '@/api/notificationSettings'

// 响应式数据
const loading = ref(false)
const showWorkflowSettings = ref(false)
const showTaskSettings = ref(false)
const showSystemSettings = ref(false)
const showPreview = ref(false)

// 时间范围选择器
const emailTimeRange = ref(['09:00', '18:00'])
const smsTimeRange = ref(['09:00', '18:00'])
const quietTimeRange = ref(['22:00', '08:00'])

// 通知设置
const settings = reactive({
  enableInSite: true,
  enableEmail: true,
  enableSms: false,
  enableWechat: false,
  enableDingtalk: false,
  enablePush: true,
  emailNotificationTime: '09:00-18:00',
  smsNotificationTime: '09:00-18:00',
  quietHoursStart: '22:00',
  quietHoursEnd: '08:00',
  enableWeekendNotifications: false,
  enableHolidayNotifications: false,
  frequencyLimitMinutes: 5,
  workflowNotifications: '{}',
  taskNotifications: '{}',
  systemNotifications: '{}'
})

// 工作流通知设置
const workflowNotifications = reactive({
  enabled: true,
  processStarted: true,
  processCompleted: true,
  processSuspended: true,
  taskAssigned: true,
  taskCompleted: true
})

// 任务通知设置
const taskNotifications = reactive({
  enabled: true,
  newTask: true,
  taskOverdue: true,
  taskDueSoon: true,
  taskCompleted: true
})

// 系统通知设置
const systemNotifications = reactive({
  enabled: true,
  maintenance: true,
  upgrade: true,
  security: true,
  featureUpdate: true
})

// 方法
const loadSettings = async () => {
  loading.value = true
  try {
    const response = await notificationSettingsApi.getUserSettings()
    if (response.success) {
      Object.assign(settings, response.data)
      
      // 解析时间范围
      if (settings.emailNotificationTime) {
        const times = settings.emailNotificationTime.split('-')
        emailTimeRange.value = [times[0], times[1]]
      }
      if (settings.smsNotificationTime) {
        const times = settings.smsNotificationTime.split('-')
        smsTimeRange.value = [times[0], times[1]]
      }
      if (settings.quietHoursStart && settings.quietHoursEnd) {
        quietTimeRange.value = [settings.quietHoursStart, settings.quietHoursEnd]
      }
      
      // 解析通知类型设置
      try {
        if (settings.workflowNotifications) {
          Object.assign(workflowNotifications, JSON.parse(settings.workflowNotifications))
        }
        if (settings.taskNotifications) {
          Object.assign(taskNotifications, JSON.parse(settings.taskNotifications))
        }
        if (settings.systemNotifications) {
          Object.assign(systemNotifications, JSON.parse(settings.systemNotifications))
        }
      } catch (e) {
        console.warn('解析通知设置失败:', e)
      }
    }
  } catch (error) {
    ElMessage.error('加载设置失败')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  try {
    const response = await notificationSettingsApi.updateUserSettings(settings)
    if (response.success) {
      ElMessage.success('设置保存成功')
    } else {
      ElMessage.error(response.error || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleReset = async () => {
  try {
    const response = await notificationSettingsApi.resetToDefault()
    if (response.success) {
      ElMessage.success('已重置为默认设置')
      loadSettings()
    } else {
      ElMessage.error(response.error || '重置失败')
    }
  } catch (error) {
    ElMessage.error('重置失败')
  }
}

const handleEmailTimeChange = (value) => {
  if (value && value.length === 2) {
    settings.emailNotificationTime = `${value[0]}-${value[1]}`
  }
}

const handleSmsTimeChange = (value) => {
  if (value && value.length === 2) {
    settings.smsNotificationTime = `${value[0]}-${value[1]}`
  }
}

const handleQuietTimeChange = (value) => {
  if (value && value.length === 2) {
    settings.quietHoursStart = value[0]
    settings.quietHoursEnd = value[1]
  }
}

const handleSaveWorkflowSettings = () => {
  settings.workflowNotifications = JSON.stringify(workflowNotifications)
  showWorkflowSettings.value = false
  ElMessage.success('工作流通知设置已保存')
}

const handleSaveTaskSettings = () => {
  settings.taskNotifications = JSON.stringify(taskNotifications)
  showTaskSettings.value = false
  ElMessage.success('任务通知设置已保存')
}

const handleSaveSystemSettings = () => {
  settings.systemNotifications = JSON.stringify(systemNotifications)
  showSystemSettings.value = false
  ElMessage.success('系统通知设置已保存')
}

const handlePreview = () => {
  showPreview.value = true
}

const handleTest = async () => {
  try {
    const response = await notificationSettingsApi.sendTestNotification({
      title: '测试通知',
      content: '这是一条测试通知，用于验证您的通知设置是否正常工作。',
      type: 'SYSTEM',
      channel: 'IN_SITE'
    })
    if (response.success) {
      ElMessage.success('测试通知发送成功')
    } else {
      ElMessage.error(response.error || '测试通知发送失败')
    }
  } catch (error) {
    ElMessage.error('测试通知发送失败')
  }
}

// 生命周期
onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.notification-settings {
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

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.notification-preview {
  max-height: 400px;
  overflow-y: auto;
}

.preview-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.preview-content h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.preview-content p {
  margin: 0 0 8px 0;
  color: #606266;
  line-height: 1.5;
}

.preview-content small {
  color: #909399;
}
</style>
