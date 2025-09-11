<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-image">
        <div class="error-number">404</div>
        <div class="error-illustration">
          <div class="cloud cloud-1"></div>
          <div class="cloud cloud-2"></div>
          <div class="cloud cloud-3"></div>
          <div class="astronaut">
            <div class="astronaut-body"></div>
            <div class="astronaut-helmet"></div>
            <div class="astronaut-arm-left"></div>
            <div class="astronaut-arm-right"></div>
          </div>
        </div>
      </div>
      
      <div class="error-info">
        <h1>页面走丢了</h1>
        <p>抱歉，您访问的页面不存在或已被移除</p>
        <div class="error-actions">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
          <el-button size="large" @click="goBack">
            <el-icon><Back /></el-icon>
            返回上页
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { HomeFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.error-content {
  text-align: center;
  color: white;
}

.error-image {
  position: relative;
  margin-bottom: 40px;

  .error-number {
    font-size: 120px;
    font-weight: 900;
    color: rgba(255, 255, 255, 0.1);
    line-height: 1;
    margin-bottom: 20px;
    text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
  }

  .error-illustration {
    position: relative;
    height: 200px;
    margin: 0 auto;
    width: 300px;

    .cloud {
      position: absolute;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 50px;
      opacity: 0.7;
      animation: float 6s ease-in-out infinite;

      &::before,
      &::after {
        content: '';
        position: absolute;
        background: rgba(255, 255, 255, 0.8);
        border-radius: 50px;
      }

      &.cloud-1 {
        width: 80px;
        height: 30px;
        top: 20px;
        left: 20px;
        animation-delay: 0s;

        &::before {
          width: 40px;
          height: 40px;
          top: -20px;
          left: 10px;
        }

        &::after {
          width: 30px;
          height: 30px;
          top: -15px;
          right: 10px;
        }
      }

      &.cloud-2 {
        width: 60px;
        height: 25px;
        top: 40px;
        right: 30px;
        animation-delay: 2s;

        &::before {
          width: 30px;
          height: 30px;
          top: -15px;
          left: 15px;
        }

        &::after {
          width: 25px;
          height: 25px;
          top: -12px;
          right: 8px;
        }
      }

      &.cloud-3 {
        width: 50px;
        height: 20px;
        top: 80px;
        left: 50px;
        animation-delay: 4s;

        &::before {
          width: 25px;
          height: 25px;
          top: -12px;
          left: 12px;
        }

        &::after {
          width: 20px;
          height: 20px;
          top: -10px;
          right: 8px;
        }
      }
    }

    .astronaut {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      animation: astronaut-float 4s ease-in-out infinite;

      .astronaut-body {
        width: 60px;
        height: 80px;
        background: #f0f0f0;
        border-radius: 30px;
        position: relative;
        margin: 0 auto;

        &::before {
          content: '';
          position: absolute;
          width: 40px;
          height: 50px;
          background: #409EFF;
          border-radius: 20px;
          top: 15px;
          left: 10px;
        }
      }

      .astronaut-helmet {
        width: 70px;
        height: 70px;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 50%;
        position: absolute;
        top: -35px;
        left: -5px;
        border: 3px solid #ddd;

        &::before {
          content: '';
          position: absolute;
          width: 50px;
          height: 50px;
          background: rgba(64, 158, 255, 0.3);
          border-radius: 50%;
          top: 10px;
          left: 10px;
        }
      }

      .astronaut-arm-left,
      .astronaut-arm-right {
        width: 20px;
        height: 40px;
        background: #f0f0f0;
        border-radius: 10px;
        position: absolute;
        top: 20px;
      }

      .astronaut-arm-left {
        left: -25px;
        transform: rotate(-20deg);
      }

      .astronaut-arm-right {
        right: -25px;
        transform: rotate(20deg);
      }
    }
  }
}

.error-info {
  h1 {
    font-size: 36px;
    font-weight: 700;
    margin: 0 0 16px 0;
    color: white;
  }

  p {
    font-size: 18px;
    margin: 0 0 32px 0;
    color: rgba(255, 255, 255, 0.8);
  }

  .error-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes astronaut-float {
  0%, 100% {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  25% {
    transform: translate(-50%, -50%) rotate(-5deg);
  }
  75% {
    transform: translate(-50%, -50%) rotate(5deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .error-image {
    .error-number {
      font-size: 80px;
    }

    .error-illustration {
      width: 250px;
      height: 150px;
    }
  }

  .error-info {
    h1 {
      font-size: 28px;
    }

    p {
      font-size: 16px;
    }

    .error-actions {
      flex-direction: column;
      align-items: center;

      .el-button {
        width: 200px;
      }
    }
  }
}
</style>