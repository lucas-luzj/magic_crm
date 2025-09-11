<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
            <image-uploader v-model="userInfo.avatar" :ext-data="{modelName:'avatar'}" />
        <div>userInfo={{ userInfo }}</div>
      <!-- 头像和基本信息区域 -->
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="showUploadDialog = true">
            <el-avatar 
              :size="120" 
              :src="userInfo.avatar" 
              class="user-avatar"
            >
              <span v-if="!userInfo.avatar">{{ userInfo.name?.charAt(0) || 'U' }}</span>
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </div>
        </div>
        <div class="user-info">
          <h2>{{ userInfo.fullName || '未设置姓名' }}</h2>
          <p class="user-email">{{ userInfo.email || '未设置邮箱' }}</p>
          <p class="user-role">{{ userInfo.role || '普通用户' }}</p>
          <p class="join-date">注册时间：{{ formatDate(userInfo.createTime) }}</p>
        </div>
      </div>

      <!-- 个人信息详情 -->
      <div class="profile-details">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card class="info-card">
              <template #header>
                <span>基本信息</span>
                <el-button type="text" @click="editBasicInfo = true">编辑</el-button>
              </template>
              <div class="info-item">
                <label>姓名：</label>
                <span>{{ userInfo.name || '未设置' }}</span>
              </div>
              <div class="info-item">
                <label>邮箱：</label>
                <span>{{ userInfo.email || '未设置' }}</span>
              </div>
              <div class="info-item">
                <label>手机号：</label>
                <span>{{ userInfo.phone || '未设置' }}</span>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card class="info-card">
              <template #header>
                <span>账户设置</span>
              </template>
              <div class="info-item">
                <label>用户角色：</label>
                <span>{{ userInfo.role || '普通用户' }}</span>
              </div>
              <div class="info-item">
                <label>账户状态：</label>
                <el-tag :type="userInfo.status === 'active' ? 'success' : 'danger'">
                  {{ userInfo.status === 'active' ? '正常' : '禁用' }}
                </el-tag>
              </div>
              <div class="info-item">
                <label>最后登录：</label>
                <span>{{ formatDate(userInfo.lastLoginAt) }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="更换头像"
      width="500px"
      :before-close="handleCloseUpload"
    >
      <div class="upload-container">
        <div class="current-avatar">
          <h4>当前头像</h4>
          <el-avatar :size="80" :src="userInfo.avatar">
            <span v-if="!userInfo.avatar">{{ userInfo.name?.charAt(0) || 'U' }}</span>
          </el-avatar>
        </div>
        
        <div class="new-avatar" v-if="previewUrl">
          <h4>新头像预览</h4>
          <el-avatar :size="80" :src="previewUrl" />
        </div>
      </div>

      <el-upload
        ref="uploadRef"
        class="avatar-uploader"
        :action="uploadAction"
        :headers="uploadHeaders"
        :show-file-list="false"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :on-change="handleFileChange"
        :auto-upload="false"
        accept="image/*"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传jpg/png文件，且不超过2MB
          </div>
        </template>
      </el-upload>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseUpload">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmUpload"
            :loading="uploading"
            :disabled="!selectedFile"
          >
            确认上传
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 基本信息编辑对话框 -->
    <el-dialog
      v-model="editBasicInfo"
      title="编辑基本信息"
      width="400px"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editBasicInfo = false">取消</el-button>
          <el-button type="primary" @click="saveBasicInfo">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera, UploadFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo, uploadAvatar, updateUserAvatar } from '@/api/user'
import { getToken } from '@/utils/auth'
import { ImageUploader} from 'lucas-my-form'

export default {
  name: 'Profile',
  components: {
    Camera,
    UploadFilled,
    ImageUploader
  },
  setup() {
    const userStore = useUserStore()
    
    // 响应式数据
    const userInfo = reactive({
      id: '',
      username:'',
      fullName: '',
      email: '',
      phone: '',
      avatar: '',
      role: '',
      status: 'active',
      createTime: '',
      lastLoginAt: ''
    })
    
    const showUploadDialog = ref(false)
    const editBasicInfo = ref(false)
    const uploading = ref(false)
    const previewUrl = ref('')
    const selectedFile = ref(null)
    
    const editForm = reactive({
      name: '',
      email: '',
      phone: ''
    })
    
    // 上传配置
    const uploadAction = ref('/api/upload/avatar')
    const uploadHeaders = ref({
      'Authorization': `Bearer ${getToken()}`
    })
    
    // 方法
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      return new Date(dateString).toLocaleString('zh-CN')
    }
    
    const loadUserInfo = async () => {
      const val = await getUserInfo()
      console.log(val)
      Object.assign(userInfo, val)
    }
    
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('上传头像图片大小不能超过 2MB!')
        return false
      }
      return true
    }
    
    const handleFileChange = (file) => {
      selectedFile.value = file.raw
      
      // 创建预览URL
      if (file.raw) {
        const reader = new FileReader()
        reader.onload = (e) => {
          previewUrl.value = e.target.result
        }
        reader.readAsDataURL(file.raw)
      }
    }
    
    const confirmUpload = async () => {
      if (!selectedFile.value) {
        ElMessage.warning('请选择要上传的图片')
        return
      }
      
      uploading.value = true
      
      try {
        // 使用通用上传接口
        const response = await uploadAvatar(selectedFile.value)
        if (response.success) {
          // 先更新数据库中的头像URL
          await updateUserAvatar(response.url)
          
          // 更新用户头像 - 使用通用上传返回的url
          userInfo.avatar = response.url
          
          // 更新store中的用户信息
          userStore.updateUserInfo({ avatar: response.url })
          
          ElMessage.success('头像上传成功')
          handleCloseUpload()
        } else {
          ElMessage.error(response.message || '头像上传失败')
        }
        
      } catch (error) {
        console.error('头像上传失败:', error)
        ElMessage.error('头像上传失败')
      } finally {
        uploading.value = false
      }
    }
    
    const handleUploadSuccess = (response) => {
      ElMessage.success('头像上传成功')
      userInfo.avatar = response.data.avatarUrl
      handleCloseUpload()
    }
    
    const handleUploadError = (error) => {
      console.error('上传失败:', error)
      ElMessage.error('头像上传失败')
    }
    
    const handleCloseUpload = () => {
      showUploadDialog.value = false
      previewUrl.value = ''
      selectedFile.value = null
    }
    
    const saveBasicInfo = async () => {
      try {
        const response = await updateUserInfo(editForm)
        Object.assign(userInfo, response.data)
        
        // 更新store中的用户信息
        userStore.updateUserInfo(response.data)
        
        ElMessage.success('信息更新成功')
        editBasicInfo.value = false
      } catch (error) {
        console.error('更新用户信息失败:', error)
        ElMessage.error('更新用户信息失败')
      }
    }
    
    // 生命周期
    onMounted(() => {
      loadUserInfo()
    })
    
    // 监听编辑对话框打开
    const openEditDialog = () => {
      editForm.name = userInfo.name
      editForm.email = userInfo.email
      editForm.phone = userInfo.phone
      editBasicInfo.value = true
    }
    
    return {
      userInfo,
      showUploadDialog,
      editBasicInfo,
      uploading,
      previewUrl,
      selectedFile,
      editForm,
      uploadAction,
      uploadHeaders,
      formatDate,
      beforeUpload,
      handleFileChange,
      confirmUpload,
      handleUploadSuccess,
      handleUploadError,
      handleCloseUpload,
      saveBasicInfo,
      openEditDialog
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.avatar-section {
  margin-right: 30px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  display: inline-block;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-avatar {
  border: 3px solid #f0f0f0;
  transition: all 0.3s;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
}

.avatar-overlay .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.user-info h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.user-info p {
  margin: 4px 0;
  color: #606266;
}

.user-email {
  font-size: 16px;
  color: #409eff;
}

.profile-details {
  margin-top: 20px;
}

.info-card {
  margin-bottom: 20px;
}

.info-card .el-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.info-item label {
  width: 80px;
  color: #606266;
  font-weight: 500;
}

.upload-container {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  text-align: center;
}

.current-avatar h4,
.new-avatar h4 {
  margin-bottom: 10px;
  color: #303133;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.el-icon--upload {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
</style>