<template>
  <div class="menu-group" :class="{ 'collapsed': isCollapsed }">
    <!-- 分组标题 -->
    <div 
      class="menu-group-header" 
      :class="{ 'collapsed': isCollapsed, 'expanded': isExpanded }"
      @click="toggleExpanded"
    >
      <div class="group-icon-wrapper">
        <el-icon class="group-icon">
          <component :is="group.icon" />
        </el-icon>
      </div>
      <div v-show="!isCollapsed" class="group-content">
        <span class="group-title">{{ group.title }}</span>
        <div class="group-indicator">
          <span class="item-count">{{ group.items.length }}</span>
        </div>
      </div>
      <el-icon 
        v-show="!isCollapsed" 
        class="expand-icon" 
        :class="{ 'expanded': isExpanded }"
      >
        <ArrowDown />
      </el-icon>
    </div>

    <!-- 分组内容 -->
    <div 
      v-show="!isCollapsed && isExpanded" 
      class="menu-group-content"
    >
      <div class="menu-items">
        <SidebarItem
          v-for="item in group.items"
          :key="item.path"
          :item="item"
          :base-path="item.path"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import SidebarItem from './SidebarItem.vue'

const props = defineProps({
  group: {
    type: Object,
    required: true
  },
  isCollapsed: {
    type: Boolean,
    default: false
  },
  accordionMode: {
    type: Boolean,
    default: false
  },
  expanded: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

// 计算展开状态
const isExpanded = computed(() => {
  return props.expanded
})

// 切换展开状态
const toggleExpanded = () => {
  if (!props.isCollapsed) {
    emit('toggle', props.group.key)
  }
}
</script>

<style lang="scss" scoped>
.menu-group {
  margin-bottom: 12px;
  position: relative;

  .menu-group-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin: 0 8px;
    border-radius: 12px;
    background: rgba(30, 41, 59, 0.4);
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
    border: 1px solid rgba(148, 163, 184, 0.1);
    position: relative;
    overflow: hidden;

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

    &:hover {
      background: rgba(30, 41, 59, 0.6);
      border-color: rgba(59, 130, 246, 0.3);
      transform: translateY(-2px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);

      &::before {
        opacity: 1;
      }

      .group-icon-wrapper {
        transform: scale(1.1);
      }
    }

    &.expanded {
      background: rgba(30, 41, 59, 0.7);
      border-color: rgba(59, 130, 246, 0.4);

      .expand-icon {
        transform: rotate(180deg);
        color: #3b82f6;
      }
    }

    &.collapsed {
      padding: 12px;
      justify-content: center;
      margin: 0 4px;

      .group-icon-wrapper {
        margin-right: 0;
      }
    }

    .group-icon-wrapper {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(147, 51, 234, 0.2));
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      transition: all 0.3s ease;
      position: relative;
      z-index: 1;

      .group-icon {
        font-size: 20px;
        color: #94a3b8;
        transition: all 0.3s ease;
      }
    }

    .group-content {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      min-width: 0;
      position: relative;
      z-index: 1;

      .group-title {
        font-size: 15px;
        font-weight: 600;
        color: #f1f5f9;
        white-space: nowrap;
        letter-spacing: 0.3px;
      }

      .group-indicator {
        .item-count {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          min-width: 20px;
          height: 20px;
          padding: 0 6px;
          border-radius: 10px;
          background: rgba(59, 130, 246, 0.2);
          color: #3b82f6;
          font-size: 11px;
          font-weight: 600;
          border: 1px solid rgba(59, 130, 246, 0.3);
        }
      }
    }

    .expand-icon {
      font-size: 14px;
      color: #94a3b8;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      z-index: 1;
    }
  }

  .menu-group-content {
    padding: 8px 0 0 0;
    animation: slideDown 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    
    .menu-items {
      display: flex;
      flex-direction: column;
      gap: 6px;
      padding: 0 8px;
    }
  }

  // 折叠状态下的特殊样式
  &.collapsed {
    .menu-group-header {
      .group-icon-wrapper {
        width: 36px;
        height: 36px;
        margin-right: 0;

        .group-icon {
          font-size: 18px;
        }
      }
    }
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-8px);
    max-height: 0;
  }
  to {
    opacity: 1;
    transform: translateY(0);
    max-height: 200px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .menu-group {
    .menu-group-header {
      padding: 10px 12px;
      margin: 0 4px;

      .group-icon-wrapper {
        width: 36px;
        height: 36px;
        margin-right: 10px;

        .group-icon {
          font-size: 18px;
        }
      }

      .group-content {
        .group-title {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
