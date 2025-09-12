<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
      <div class="bg-shape bg-shape-3"></div>
    </div>
    
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="32" color="#409eff">
            <Platform />
          </el-icon>
          <span class="logo-text">Denwon CRM</span>
        </div>
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-subtitle">企业客户关系管理系统</p>
      </div>
      
      <el-form 
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item v-if="showCaptcha" prop="captcha">
          <div class="captcha-container">
            <el-input
              v-model="loginForm.captcha"
              placeholder="请输入验证码"
              size="large"
              :prefix-icon="Key"
              clearable
            />
            <img 
              :src="captchaUrl" 
              alt="验证码" 
              class="captcha-img"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>
        
        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p class="copyright">© 2025 Denwon CRM. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Key, Platform } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const loginFormRef = ref()

// 登录表单
const loginForm = reactive({
  username: 'admin',
  password: 'Admin@123',
  captcha: '',
  captchaKey: '',
  rememberMe: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为 4 个字符', trigger: 'blur' }
  ]
}

// 状态
const loading = ref(false)
const showCaptcha = ref(false)
const captchaUrl = ref('')
const loginAttempts = ref(0)

// 处理登录
const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  
  try {
    const success = await authStore.handleLogin(loginForm)
    if (success) {
      loginAttempts.value = 0
    } else {
      loginAttempts.value++
      if (loginAttempts.value >= 3) {
        showCaptcha.value = true
        refreshCaptcha()
      }
    }
  } finally {
    loading.value = false
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  const timestamp = new Date().getTime()
  captchaUrl.value = `/api/auth/captcha?t=${timestamp}`
  loginForm.captchaKey = timestamp.toString()
}

// 初始化
onMounted(() => {
  // 检查是否已登录
  if (authStore.isLoggedIn) {
    router.push('/')
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  
  .bg-shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    animation: float 20s infinite ease-in-out;
    
    &-1 {
      width: 400px;
      height: 400px;
      background: #fff;
      top: -200px;
      right: -100px;
      animation-delay: 0s;
    }
    
    &-2 {
      width: 300px;
      height: 300px;
      background: #fff;
      bottom: -150px;
      left: -100px;
      animation-delay: 5s;
    }
    
    &-3 {
      width: 200px;
      height: 200px;
      background: #fff;
      top: 50%;
      left: 10%;
      animation-delay: 10s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  33% {
    transform: translateY(-30px) rotate(120deg);
  }
  66% {
    transform: translateY(30px) rotate(240deg);
  }
}

.login-card {
  position: relative;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    
    .logo-text {
      margin-left: 10px;
      font-size: 24px;
      font-weight: 600;
      color: #409eff;
    }
  }
  
  .login-title {
    margin: 0 0 10px;
    font-size: 28px;
    font-weight: 600;
    color: #303133;
  }
  
  .login-subtitle {
    margin: 0;
    color: #909399;
    font-size: 14px;
  }
}

.login-form {
  .captcha-container {
    display: flex;
    align-items: center;
    width: 100%;
    
    .el-input {
      flex: 1;
    }
    
    .captcha-img {
      width: 100px;
      height: 40px;
      margin-left: 10px;
      cursor: pointer;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
    }
  }
  
  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  
  .login-btn {
    width: 100%;
    font-size: 16px;
    letter-spacing: 2px;
  }
}

.login-footer {
  margin-top: 30px;
  text-align: center;
  
  .copyright {
    margin: 0;
    color: #909399;
    font-size: 12px;
  }
}

// 响应式设计
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    padding: 30px 20px;
  }
  
  .login-header {
    .login-title {
      font-size: 24px;
    }
  }
}
</style>