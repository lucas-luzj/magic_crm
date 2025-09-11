<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="login-bg">
      <div class="bg-animation">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
          <div class="shape shape-5"></div>
        </div>
      </div>
    </div>

    <!-- 登录表单 -->
    <div class="login-form-container">
      <div class="login-form">
        <div class="login-header">
          <div class="logo">
            <el-icon class="logo-icon">
              <MagicStick />
            </el-icon>
            <h1>Magic CRM</h1>
          </div>
          <p class="subtitle">智能客户管理系统</p>
        </div>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form-content"
          @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" :prefix-icon="User" clearable />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" :prefix-icon="Lock"
              show-password clearable />
          </el-form-item>

          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="loginForm.rememberMe">
                记住我
              </el-checkbox>
              <el-link type="primary" :underline="false">
                忘记密码？
              </el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

      </div>
    </div>

    <!-- 版权信息 -->
    <div class="copyright">
      <p>&copy; 2024 Magic CRM. All rights reserved.</p>
    </div>
  </div>
</template>

<script setup>
import { User, Lock, MagicStick } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  rememberMe: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    loading.value = true
    try {
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password
      })
      ElMessage.success('登录成功')
      router.push('/')
    } finally {
      loading.value = false
    }
  })
}

</script>

<style lang="scss" scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;

  .bg-animation {
    position: relative;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="10" cy="60" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="90" cy="40" r="0.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
      opacity: 0.3;
    }
  }

  .floating-shapes {
    position: absolute;
    width: 100%;
    height: 100%;

    .shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: float 6s ease-in-out infinite;

      &.shape-1 {
        width: 80px;
        height: 80px;
        top: 20%;
        left: 10%;
        animation-delay: 0s;
      }

      &.shape-2 {
        width: 60px;
        height: 60px;
        top: 60%;
        left: 80%;
        animation-delay: 2s;
      }

      &.shape-3 {
        width: 40px;
        height: 40px;
        top: 80%;
        left: 20%;
        animation-delay: 4s;
      }

      &.shape-4 {
        width: 100px;
        height: 100px;
        top: 10%;
        right: 10%;
        animation-delay: 1s;
      }

      &.shape-5 {
        width: 50px;
        height: 50px;
        top: 40%;
        left: 5%;
        animation-delay: 3s;
      }
    }
  }
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-form-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 400px;
  padding: 0 20px;
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px;

    .logo-icon {
      font-size: 32px;
      color: #409EFF;
      margin-right: 10px;
    }

    h1 {
      font-size: 28px;
      font-weight: 700;
      color: #2c3e50;
      margin: 0;
      background: linear-gradient(135deg, #409EFF, #667eea);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .subtitle {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
}

.login-form-content {
  .el-form-item {
    margin-bottom: 24px;

    :deep(.el-input__wrapper) {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      border: 1px solid #e4e7ed;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      &.is-focus {
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      }
    }
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
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409EFF, #667eea);
  border: none;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  color: #666;
  font-size: 14px;
}

.copyright {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  z-index: 2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form {
    margin: 20px;
    padding: 30px 20px;
  }

  .login-header .logo h1 {
    font-size: 24px;
  }
}
</style>