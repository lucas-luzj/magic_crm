# Denwon CRM - 企业客户关系管理系统

## 项目简介

Denwon CRM 是一个专业的B2B销售与服务全链路数字化CRM管理系统，实现从线索到售后的完整业务闭环管理。系统采用前后端分离架构，提供优美的界面设计、精炼的代码实现和全面完整的功能模块。

## 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.0
- **语言**: Java 17
- **数据库**: PostgreSQL 17+
- **ORM**: Spring Data JPA
- **安全**: Spring Security + JWT
- **工作流**: Camunda 7.20
- **缓存**: Caffeine
- **文件存储**: MinIO
- **API文档**: SpringDoc OpenAPI

### 前端技术
- **框架**: Vue 3.4 + Composition API
- **构建工具**: Vite 5.1
- **UI组件库**: Element Plus 2.6
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.3
- **HTTP客户端**: Axios 1.6
- **图表**: ECharts 5.5
- **样式**: Sass + TailwindCSS

## 核心功能模块

### 1. 客户管理
- 客户信息管理（360度视图）
- 公海池管理（自动回收、认领、分配）
- 联系人管理
- 客户跟进记录
- 客户标签和分级

### 2. 线索管理
- 线索采集（多渠道）
- 智能去重
- 自动分配规则
- 线索评分
- 转化跟踪

### 3. 商机管理
- 商机阶段管理
- 多版本报价
- 商机看板
- 赢率预测
- 竞争分析

### 4. 合同管理
- 合同审批流程
- 合同修订管理
- 订单管理
- 发货管理
- 合同归档

### 5. 项目管理
- 项目立项
- 里程碑管理
- 任务分配
- 甘特图
- 工时统计

### 6. 财务管理
- 应收管理
- 回款管理
- 发票管理
- 提成计算
- 财务报表

### 7. 产品管理
- 产品目录
- 价格策略
- 库存管理（轻量级）
- 产品组合

### 8. 采购管理
- 供应商管理
- 采购合同
- 入库管理
- 采购分摊

### 9. 售后服务
- 设备台账
- 服务工单
- SLA管理
- 知识库
- 客户满意度

### 10. 办公协同
- 任务管理
- 日程管理
- 通知中心
- 审批中心
- 团队协作

### 11. 报表分析
- 销售报表
- 财务报表
- 服务报表
- 自定义报表
- 数据看板

### 12. 系统管理
- 用户管理
- 角色权限
- 组织架构
- 数据字典
- 工作流配置
- 系统日志

### 13. AI智能功能
- 智能线索评分
- 商机赢率预测
- 智能任务分配
- 异常检测
- 自动化报告
- 语音识别与处理

## 项目结构

```
denwon-crm/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java源代码
│   │   │   └── resources/  # 配置文件和资源
│   │   └── test/           # 测试代码
│   └── pom.xml             # Maven配置
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── assets/         # 静态资源
│   │   ├── components/     # 公共组件
│   │   ├── layout/         # 布局组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   ├── styles/         # 样式文件
│   │   ├── utils/          # 工具函数
│   │   └── views/          # 页面组件
│   ├── package.json        # 依赖配置
│   └── vite.config.js      # Vite配置
├── database/                # 数据库脚本
└── docs/                    # 项目文档
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- PostgreSQL 17+
- Maven 3.6+

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE denwon_crm;
```

2. 修改数据库配置
编辑 `backend/src/main/resources/application.yml`，配置数据库连接信息

3. 启动后端服务
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端应用将在 http://localhost:5173 启动

### 默认账号

- 用户名: admin
- 密码: Admin@123

## 系统特性

### 安全特性
- JWT Token认证
- 细粒度权限控制
- 数据加密传输
- SQL注入防护
- XSS攻击防护
- 操作审计日志

### 性能优化
- 数据库索引优化
- 接口响应缓存
- 前端路由懒加载
- 图片懒加载
- Gzip压缩
- CDN加速

### 用户体验
- 响应式设计
- 多主题切换
- 国际化支持
- 快捷键操作
- 实时通知
- 离线缓存

## API接口规范

### 请求格式
所有接口统一使用POST方法，请求体为JSON格式：

```javascript
// 列表查询
POST /api/module/list
{
  "pageIndex": 0,
  "pageSize": 10,
  "filters": {...},
  "sort": "field,desc"
}

// 单条查询
POST /api/module/get
{
  "id": 1
}

// 新增/更新
POST /api/module/update
{
  "id": 0,  // 0表示新增，>0表示更新
  ...
}

// 删除
POST /api/module/delete
{
  "id": 1
}
```

### 响应格式
```javascript
// 成功响应
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": {...}
}

// 错误响应
{
  "code": 400,
  "success": false,
  "message": "错误信息",
  "error": "详细错误描述"
}

// 分页响应
{
  "code": 200,
  "success": true,
  "message": "查询成功",
  "data": {
    "totalCount": 100,
    "pageIndex": 0,
    "pageCount": 10,
    "records": [...]
  }
}
```

## 部署说明

### Docker部署
```bash
# 构建镜像
docker build -t denwon-crm-backend ./backend
docker build -t denwon-crm-frontend ./frontend

# 运行容器
docker-compose up -d
```

### 传统部署
1. 后端打包
```bash
cd backend
mvn clean package
java -jar target/crm-1.0.0.jar
```

2. 前端打包
```bash
cd frontend
npm run build
# 将dist目录部署到nginx
```

## 开发规范

详见 [DEVELOPMENT_STANDARDS.md](./DEVELOPMENT_STANDARDS.md)

## 更新日志

### v1.0.0 (2025-01-01)
- 初始版本发布
- 完成核心功能模块
- 实现基础权限管理
- 集成工作流引擎

## 贡献指南

欢迎提交Issue和Pull Request

## 许可证

本项目采用 MIT 许可证

## 联系我们

- 邮箱: support@denwon.com
- 官网: https://www.denwon.com

## 致谢

感谢所有为本项目做出贡献的开发者！