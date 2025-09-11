<template>
  <div class="workflow-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>工作流管理</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="workflow-card" @click="navigateTo('/workflow/process-definitions')">
            <div class="workflow-item">
              <el-icon class="workflow-icon" size="48" color="#409EFF">
                <Document />
              </el-icon>
              <h3>流程定义管理</h3>
              <p>管理和部署BPMN流程定义</p>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover" class="workflow-card" @click="navigateTo('/workflow/process-designer')">
            <div class="workflow-item">
              <el-icon class="workflow-icon" size="48" color="#67C23A">
                <Edit />
              </el-icon>
              <h3>流程设计器</h3>
              <p>可视化设计BPMN流程图</p>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover" class="workflow-card" @click="navigateTo('/workflow/process-instances')">
            <div class="workflow-item">
              <el-icon class="workflow-icon" size="48" color="#E6A23C">
                <Operation />
              </el-icon>
              <h3>流程实例管理</h3>
              <p>监控和管理运行中的流程实例</p>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover" class="workflow-card" @click="navigateTo('/workflow/tasks')">
            <div class="workflow-item">
              <el-icon class="workflow-icon" size="48" color="#F56C6C">
                <List />
              </el-icon>
              <h3>任务管理</h3>
              <p>处理待办和已完成任务</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Document, Edit, Operation, List } from '@element-plus/icons-vue'

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.workflow-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.workflow-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 200px;
}

.workflow-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.workflow-item {
  text-align: center;
  padding: 20px;
}

.workflow-icon {
  margin-bottom: 15px;
}

.workflow-item h3 {
  margin: 15px 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.workflow-item p {
  color: #909399;
  font-size: 14px;
  margin: 0;
  line-height: 1.4;
}
</style>