<template>
  <div class="menu-item" :class="{ 'active': isActive }" @click="handleMenuClick">
    <div class="menu-item-content">
      <div class="menu-icon-wrapper">
        <el-icon class="menu-icon">
          <component :is="item.icon" />
        </el-icon>
      </div>
      <span class="menu-title">{{ item.title }}</span>
      <div v-if="isActive" class="active-indicator"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useTabsStore } from '@/stores/tabs'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

const router = useRouter()
const route = useRoute()
const tabsStore = useTabsStore()

// 解析完整路径
const resolvePath = computed(() => {
  if (props.item.path.startsWith('/')) {
    return props.item.path
  }
  return `${props.basePath}/${props.item.path}`.replace(/\/+/g, '/')
})

// 判断是否为当前激活的菜单
const isActive = computed(() => {
  return route.path === resolvePath.value
})

// 处理菜单点击
const handleMenuClick = () => {
  if (resolvePath.value !== route.path) {
    // 添加到标签页
    const routeInfo = {
      name: props.item.name,
      path: resolvePath.value,
      meta: {
        title: props.item.title,
        icon: props.item.icon
      }
    }
    tabsStore.addView(routeInfo)
    
    // 路由跳转
    router.push(resolvePath.value)
  }
}
</script>

<style lang="scss" scoped>
.menu-item {
  margin: 0 0 6px 0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  .menu-item-content {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: rgba(30, 41, 59, 0.3);
    border: 1px solid rgba(148, 163, 184, 0.08);
    border-radius: 10px;
    transition: all 0.3s ease;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(147, 51, 234, 0.1));
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .menu-icon-wrapper {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      background: rgba(59, 130, 246, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      transition: all 0.3s ease;
      position: relative;
      z-index: 1;

      .menu-icon {
        font-size: 16px;
        color: #94a3b8;
        transition: all 0.3s ease;
      }
    }

    .menu-title {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
      color: #cbd5e1;
      white-space: nowrap;
      transition: all 0.3s ease;
      position: relative;
      z-index: 1;
    }

    .active-indicator {
      width: 4px;
      height: 20px;
      background: linear-gradient(to bottom, #3b82f6, #8b5cf6);
      border-radius: 2px;
      margin-left: 8px;
      position: relative;
      z-index: 1;
    }
  }

  &:hover {
    .menu-item-content {
      background: rgba(30, 41, 59, 0.5);
      border-color: rgba(59, 130, 246, 0.3);
      transform: translateX(4px);

      &::before {
        opacity: 1;
      }

      .menu-icon-wrapper {
        background: rgba(59, 130, 246, 0.2);
        transform: scale(1.1);

        .menu-icon {
          color: #3b82f6;
        }
      }

      .menu-title {
        color: #f1f5f9;
      }
    }
  }

  &.active {
    .menu-item-content {
      background: rgba(59, 130, 246, 0.15);
      border-color: rgba(59, 130, 246, 0.4);
      box-shadow: 0 4px 20px rgba(59, 130, 246, 0.2);

      &::before {
        opacity: 1;
      }

      .menu-icon-wrapper {
        background: rgba(59, 130, 246, 0.3);

        .menu-icon {
          color: #3b82f6;
        }
      }

      .menu-title {
        color: #f8fafc;
        font-weight: 600;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .menu-item {
    .menu-item-content {
      padding: 10px 12px;

      .menu-icon-wrapper {
        width: 28px;
        height: 28px;
        margin-right: 10px;

        .menu-icon {
          font-size: 14px;
        }
      }

      .menu-title {
        font-size: 13px;
      }
    }
  }
}
</style>