<template>
  <div class="notification-settings" v-loading="loading">
    <el-form :model="settings" label-width="120px">
      <!-- 通知开关 -->
      <el-card class="settings-card" shadow="never">
        <template #header>
          <h4>通知开关</h4>
        </template>
        
        <el-form-item label="启用通知">
          <el-switch v-model="settings.enabled" />
        </el-form-item>
        
        <el-form-item label="桌面通知">
          <el-switch v-model="settings.desktopNotification" />
        </el-form-item>
        
        <el-form-item label="邮件通知">
          <el-switch v-model="settings.emailNotification" />
        </el-form-item>
        
        <el-form-item label="短信通知">
          <el-switch v-model="settings.smsNotification" />
        </el-form-item>
      </el-card>

      <!-- 通知类型设置 -->
      <el-card class="settings-card" shadow="never">
        <template #header>
          <h4>通知类型设置</h4>
        </template>
        
        <el-form-item label="任务通知">
          <el-switch v-model="settings.types.task" />
        </el-form-item>
        
        <el-form-item label="流程通知">
          <el-switch v-model="settings.types.process" />
        </el-form-item>
        
        <el-form-item label="系统通知">
          <el-switch v-model="settings.types.system" />
        </el-form-item>
        
        <el-form-item label="警告通知">
          <el-switch v-model="settings.types.warning" />
        </el-form-item>
        
        <el-form-item label="成功通知">
          <el-switch v-model="settings.types.success" />
        </el-form-item>
        
        <el-form-item label="信息通知">
          <el-switch v-model="settings.types.info" />
        </el-form-item>
      </el-card>

      <!-- 通知时间设置 -->
      <el-card class="settings-card" shadow="never">
        <template #header>
          <h4>通知时间设置</h4>
        </template>
        
        <el-form-item label="免打扰时间">
          <el-time-picker
            v-model="settings.quietHours"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>
        
        <el-form-item label="工作日通知">
          <el-checkbox-group v-model="settings.workDays">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-card>

      <!-- 通知频率设置 -->
      <el-card class="settings-card" shadow="never">
        <template #header>
          <h4>通知频率设置</h4>
        </template>
        
        <el-form-item label="通知频率">
          <el-select v-model="settings.frequency">
            <el-option label="实时通知" value="realtime" />
            <el-option label="每5分钟" value="5min" />
            <el-option label="每15分钟" value="15min" />
            <el-option label="每30分钟" value="30min" />
            <el-option label="每小时" value="1hour" />
            <el-option label="每天汇总" value="daily" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="最大通知数">
          <el-input-number 
            v-model="settings.maxNotifications" 
            :min="1" 
            :max="100" 
            controls-position="right"
          />
          <span class="form-tip">超过此数量将合并为汇总通知</span>
        </el-form-item>
      </el-card>

      <!-- 通知渠道设置 -->
      <el-card class="settings-card" shadow="never">
        <template #header>
          <h4>通知渠道设置</h4>
        </template>
        
        <el-form-item label="站内信">
          <el-switch v-model="settings.channels.inApp" />
        </el-form-item>
        
        <el-form-item label="邮件地址">
          <el-input v-model="settings.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        
        <el-form-item label="手机号码">
          <el-input v-model="settings.phone" placeholder="请输入手机号码" />
        </el-form-item>
        
        <el-form-item label="微信通知">
          <el-switch v-model="settings.channels.wechat" />
        </el-form-item>
        
        <el-form-item label="钉钉通知">
          <el-switch v-model="settings.channels.dingtalk" />
        </el-form-item>
      </el-card>
    </el-form>

    <!-- 操作按钮 -->
    <div class="settings-actions">
      <el-button @click="resetSettings">重置</el-button>
      <el-button type="primary" @click="saveSettings" :loading="saving">
        保存设置
      </el-button>
      <el-button @click="testNotification">发送测试通知</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { notificationApi } from '@/api/notification'

// Emits
const emit = defineEmits(['settings-updated'])

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const settings = reactive({
  enabled: true,
  desktopNotification: true,
  emailNotification: false,
  smsNotification: false,
  types: {
    task: true,
    process: true,
    system: true,
    warning: true,
    success: true,
    info: true
  },
  quietHours: null,
  workDays: ['1', '2', '3', '4', '5'],
  frequency: 'realtime',
  maxNotifications: 50,
  channels: {
    inApp: true,
    wechat: false,
    dingtalk: false
  },
  email: '',
  phone: ''
})

// 默认设置
const defaultSettings = {
  enabled: true,
  desktopNotification: true,
  emailNotification: false,
  smsNotification: false,
  types: {
    task: true,
    process: true,
    system: true,
    warning: true,
    success: true,
    info: true
  },
  quietHours: null,
  workDays: ['1', '2', '3', '4', '5'],
  frequency: 'realtime',
  maxNotifications: 50,
  channels: {
    inApp: true,
    wechat: false,
    dingtalk: false
  },
  email: '',
  phone: ''
}

// 页面初始化
onMounted(async () => {
  await loadSettings()
})

/**
 * 加载设置
 */
const loadSettings = async () => {
  try {
    loading.value = true
    const response = await notificationApi.getNotificationSettings()
    
    if (response.data && response.data.data) {
      Object.assign(settings, response.data.data)
    }
  } catch (error) {
    console.error('加载通知设置失败:', error)
    ElMessage.warning('加载通知设置失败，使用默认设置')
  } finally {
    loading.value = false
  }
}

/**
 * 保存设置
 */
const saveSettings = async () => {
  try {
    saving.value = true
    
    // 验证设置
    if (settings.emailNotification && !settings.email) {
      ElMessage.error('启用邮件通知时，请填写邮箱地址')
      return
    }
    
    if (settings.smsNotification && !settings.phone) {
      ElMessage.error('启用短信通知时，请填写手机号码')
      return
    }
    
    await notificationApi.updateNotificationSettings(settings)
    ElMessage.success('通知设置已保存')
    emit('settings-updated')
  } catch (error) {
    console.error('保存通知设置失败:', error)
    ElMessage.error('保存通知设置失败')
  } finally {
    saving.value = false
  }
}

/**
 * 重置设置
 */
const resetSettings = () => {
  Object.assign(settings, defaultSettings)
  ElMessage.success('设置已重置为默认值')
}

/**
 * 发送测试通知
 */
const testNotification = async () => {
  try {
    const testData = {
      title: '测试通知',
      message: '这是一条测试通知，用于验证通知设置是否正常工作。',
      type: 'info',
      channels: settings.channels
    }
    
    await notificationApi.sendTestNotification(testData)
    ElMessage.success('测试通知已发送')
  } catch (error) {
    console.error('发送测试通知失败:', error)
    ElMessage.error('发送测试通知失败')
  }
}
</script>

<style lang="scss" scoped>
.notification-settings {
  .settings-card {
    margin-bottom: 20px;
    
    .el-card__header {
      h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }
  }
  
  .form-tip {
    margin-left: 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
  
  .settings-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .notification-settings {
    .settings-actions {
      flex-direction: column;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
