<template>
  <div class="user-selector">
    <el-select
      v-model="selectedUser"
      filterable
      remote
      reserve-keyword
      placeholder="请输入用户名搜索"
      :remote-method="searchUsers"
      :loading="loading"
      style="width: 100%"
      @change="handleUserChange"
    >
      <el-option
        v-for="user in users"
        :key="user.id"
        :label="`${user.username} (${user.realName || user.username})`"
        :value="user.username"
      >
        <div class="user-option">
          <el-avatar :size="24" :src="user.avatar">
            {{ user.realName ? user.realName.charAt(0) : user.username.charAt(0) }}
          </el-avatar>
          <div class="user-info">
            <div class="user-name">{{ user.realName || user.username }}</div>
            <div class="user-username">@{{ user.username }}</div>
          </div>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '@/api/user'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请选择用户'
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'change'])

// 响应式数据
const selectedUser = ref(props.modelValue)
const users = ref([])
const loading = ref(false)

// 搜索用户
const searchUsers = async (query) => {
  if (!query) {
    users.value = []
    return
  }

  try {
    loading.value = true
    const response = await userApi.searchUsers({
      keyword: query,
      page: 1,
      size: 20
    })
    users.value = response.data?.records || []
  } catch (error) {
    console.error('搜索用户失败:', error)
    users.value = []
  } finally {
    loading.value = false
  }
}

// 处理用户选择变化
const handleUserChange = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
}

// 初始化时加载一些用户
onMounted(async () => {
  try {
    const response = await userApi.getUsers({
      page: 1,
      size: 10
    })
    users.value = response.data?.records || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
})

// 监听props变化
watch(() => props.modelValue, (newValue) => {
  selectedUser.value = newValue
})
</script>

<style scoped>
.user-selector {
  width: 100%;
}

.user-option {
  display: flex;
  align-items: center;
  padding: 5px 0;
}

.user-info {
  margin-left: 10px;
  flex: 1;
}

.user-name {
  font-size: 14px;
  color: #303133;
  line-height: 1.2;
}

.user-username {
  font-size: 12px;
  color: #909399;
  line-height: 1.2;
}
</style>