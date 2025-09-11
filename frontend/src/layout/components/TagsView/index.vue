<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper" ref="scrollbarRef">
      <div class="tags-view-content" ref="tagsRef">
        <router-link
          v-for="tag in visitedViews"
          :key="tag.path"
          :to="{ path: tag.path }"
          class="tags-view-item"
          :class="{ 'active': isActive(tag) }"
          @contextmenu.prevent="openMenu(tag, $event)"
        >
          <span class="tag-title">{{ tag.title }}</span>
          <el-icon
            v-if="!isAffix(tag)"
            class="tag-close"
            @click.prevent.stop="closeSelectedTag(tag)"
          >
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>

    <!-- 右键菜单 -->
    <ul
      v-show="visible"
      :style="{ left: left + 'px', top: top + 'px' }"
      class="contextmenu"
    >
      <li @click="refreshSelectedTag(selectedTag)">
        <el-icon><Refresh /></el-icon>
        刷新页面
      </li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">
        <el-icon><Close /></el-icon>
        关闭标签
      </li>
      <li @click="closeOthersTags">
        <el-icon><Remove /></el-icon>
        关闭其他
      </li>
      <li @click="closeAllTags(selectedTag)">
        <el-icon><CircleClose /></el-icon>
        关闭所有
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted,onUnmounted,ref, computed, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Close, Refresh, Remove, CircleClose } from '@element-plus/icons-vue'
import { useTabsStore } from '@/stores/tabs'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()

const scrollbarRef = ref()
const tagsRef = ref()

// 右键菜单相关
const visible = ref(false)
const top = ref(0)
const left = ref(0)
const selectedTag = ref({})

const visitedViews = computed(() => tabsStore.visitedViews)

// 判断标签是否激活
const isActive = (tag) => {
  return tag.path === route.path
}

// 判断是否为固定标签
const isAffix = (tag) => {
  return tag.meta && tag.meta.affix
}

// 添加标签
const addTags = () => {
  const { name, path, meta } = route
  if (name) {
    tabsStore.addView({ name, path, meta })
  }
}

// 关闭选中的标签
const closeSelectedTag = (view) => {
  tabsStore.delView(view).then(({ visitedViews }) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}

// 刷新选中的标签
const refreshSelectedTag = (view) => {
  tabsStore.delCachedView(view).then(() => {
    const { fullPath } = view
    nextTick(() => {
      router.replace({
        path: '/redirect' + fullPath
      })
    })
  })
}

// 关闭其他标签
const closeOthersTags = () => {
  router.push(selectedTag.value)
  tabsStore.delOthersViews(selectedTag.value).then(() => {
    moveToCurrentTag()
  })
}

// 关闭所有标签
const closeAllTags = (view) => {
  tabsStore.delAllViews().then(({ visitedViews }) => {
    if (visitedViews.some(tag => tag.path === view.path)) {
      return
    }
    toLastView(visitedViews, view)
  })
}

// 跳转到最后一个视图
const toLastView = (visitedViews, view) => {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.fullPath || latestView.path)
  } else {
    // 如果没有标签了，跳转到首页
    if (view.name === 'Dashboard') {
      // 如果关闭的是首页，重新加载
      router.replace({ path: '/redirect' + view.fullPath })
    } else {
      router.push('/')
    }
  }
}

// 移动到当前标签
const moveToCurrentTag = () => {
  nextTick(() => {
    const tags = tagsRef.value?.querySelectorAll('.tags-view-item') || []
    for (const tag of tags) {
      if (tag.to?.path === route.path) {
        scrollbarRef.value?.setScrollLeft(tag.offsetLeft - 50)
        break
      }
    }
  })
}

// 打开右键菜单
const openMenu = (tag, e) => {
  const menuMinWidth = 105
  const offsetLeft = tagsRef.value?.getBoundingClientRect().left || 0
  const offsetWidth = tagsRef.value?.offsetWidth || 0
  const maxLeft = offsetWidth - menuMinWidth
  const left_ = e.clientX - offsetLeft + 15

  if (left_ > maxLeft) {
    left.value = maxLeft
  } else {
    left.value = left_
  }

  top.value = e.clientY - 50
  visible.value = true
  selectedTag.value = tag
}

// 关闭右键菜单
const closeMenu = () => {
  visible.value = false
}

// 监听路由变化
watch(route, () => {
  addTags()
  moveToCurrentTag()
})

// 监听点击事件，关闭右键菜单
watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

onMounted(() => {
  addTags()
})

onUnmounted(() => {
  document.body.removeEventListener('click', closeMenu)
})
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 40px;
  width: 100%;
  background: var(--bg-color);
  border-bottom: 1px solid var(--border-color);
  position: relative;
}

.tags-view-wrapper {
  height: 100%;

  :deep(.el-scrollbar__bar) {
    bottom: 0;
  }

  :deep(.el-scrollbar__wrap) {
    height: 49px;
  }
}

.tags-view-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 6px;
  gap: 4px;
}

.tags-view-item {
  display: inline-flex;
  align-items: center;
  position: relative;
  cursor: pointer;
  height: 28px;
  line-height: 28px;
  border: 1px solid #d8dce5;
  color: #495057;
  background: #fff;
  padding: 0 8px;
  font-size: 12px;
  margin-right: 4px;
  border-radius: 4px;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  white-space: nowrap;

  &:hover {
    background-color: #ecf5ff;
    border-color: #b3d8ff;
    color: #409eff;
  }

  &.active {
    background-color: #409eff;
    color: #fff;
    border-color: #409eff;

    &::before {
      content: '';
      background: #fff;
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      position: relative;
      margin-right: 4px;
    }
  }

  .tag-title {
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .tag-close {
    width: 16px;
    height: 16px;
    margin-left: 4px;
    border-radius: 50%;
    text-align: center;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    transform-origin: 100% 50%;

    &:hover {
      background-color: rgba(0, 0, 0, 0.16);
      color: #fff;
    }
  }

  &:not(.active) .tag-close:hover {
    background-color: #b4bccc;
    color: #fff;
  }
}

.contextmenu {
  margin: 0;
  background: #fff;
  z-index: 3000;
  position: absolute;
  list-style-type: none;
  padding: 5px 0;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 400;
  color: #333;
  box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);

  li {
    margin: 0;
    padding: 7px 16px;
    cursor: pointer;
    display: flex;
    align-items: center;

    &:hover {
      background: #ecf5ff;
      color: #409eff;
    }

    .el-icon {
      margin-right: 8px;
      font-size: 14px;
    }
  }
}
</style>