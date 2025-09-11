# Denwon CRM 软件开发需求说明书（V2 合并增强版）

版本：V2  
日期：2025-08-28  
来源与融合：  
- docs/详细功能需求.md（V1）  
- docs/DenwonCRM软件需求说明书.md（V1.4）  
- docs/DenWonCRM模块功能说明.md  
- 客户访谈补充（公海/私海、跨线/跨区转交与一次性奖励、项目↔合同N-N、分期回款与采购N-N、合同修订重算、安装/培训记录、提成负数冲抵、移动端/微信、语音ASR+LLM+MCP、AI洞察/周月报）

---

## 0. 概览与目标

- 目标：实现B2B销售与服务全链路数字化：线索→客户→商机/报价→合同/订单→采购/入出库→安装/验收→回款/发票→设备台账→售后工单→提成/奖励；统一权限、流程与字典；移动优先与AI赋能。
- 惊喜价值：
  - AI经营Narrative（周/月自动经营报告+可解释洞察）
  - 机会赢率预测与下一步建议
  - 语音拜访一键成文（ASR+LLM）与MCP安全落库
  - 异常检测（修订频繁/毛利波动/负数提成/阶段停滞）
  - 智能任务中心（到期/超期/未跟进/SLA预警）
  - 附近客户与路线规划（可选）
- 范围（V2）：组织/权限、数据字典、公海/私海、客户/线索、商机/报价、项目、产品/价格/库存（轻量）、合同/修订/回款/发票、采购N-N/入出库、安装/验收、设备/售后、办公、流程/规则/通知、报表/看板、移动端（微信OA/小程序）、AI中台、提成与一次性奖励。
- 非范围（V2不含）：深度成本核算、WMS、总账会计、完整HR套件。

---

## 1. 角色与组织权限

- 组织结构：集团/公司/事业部/部门/团队/个人（6级树）
- 角色：系统管理员、总经理、销售经理、销售、技术支持、服务工程师、行政、财务、访客
- 数据范围：本人/本部门/部门含下级/指定组织/全部
- 动作权限：查看/新建/编辑/删除/导入/导出/转移/共享/分配/认领（对象级）
- 字段与记录级：价格/毛利/联系方式/税号等敏感字段可独立授权；拥有者+协作人模型；共享只读或受限编辑
- 审批授权：节点候选、代理、加签、转审；按金额/折扣/事业部/区域路由
- 审计：对象变更、审批日志、登录与操作日志、导出下载日志（带水印与用途）

---

## 2. 业务关键场景（能覆盖验收）

1) 公海认领与私海沉淀  
- 入公海：线索/客户统一收集；私海仅本人+上级可见  
- 规则：未跟进/未成单天数、离职释放、限额认领、防撞单  
- 申请共享：对已有客户申请共享，审批后可见基础信息并建立自己侧的项目/行动/订单

2) 线索转化与分配  
- 来源：手工/导入/API/网站表单/名片OCR/语音  
- 去重：手机号/邮箱/公司名+地区  
- 分配：区域/来源/轮询/权重/余额  
- 转化：一键生成客户/联系人/商机/任务（可选），保留来源链路与幂等

3) 跨线/跨区转交与一次性奖励  
- 项目因区域或产品线流转，提交审批  
- 审批通过生成一次性奖励应计并消息通知

4) 商机与报价（多版本）  
- 阶段模板按事业部配置，阶段出口条件（纪要/立项/技术确认）  
- 多版本报价、版本比对、价税分离、折扣分级审批、打印模板

5) 商机→合同（N-N）  
- 一项目多合同或多项目合并一合同；合同产品明细形成基线

6) 合同修订与自动重算  
- 所有变更以“修订单”记录（增补/变更/终止）  
- 详情默认最新有效视图；重算分期计划、回款目标、提成基数，可产生负数冲抵

7) 采购与分摊（销售合同 N-N 采购合同）  
- 多销售合同合并采购或分摊到多采购  
- 到货入库→允许出库与安装；与序列号及设备台账联动

8) 出库与安装/验收  
- 出库单打印与序列号追踪  
- 安装/培训/验收记录驱动设备台账生成（可后补序列）

9) 回款/发票闭环与逾期催收  
- 模板生成分期计划；到账登记与多对多核销；首逾/续逾催收与罚息规则  
- 开票申请/审批/登记/红冲；与合同/订单/回款关联

10) 售后工单与SLA  
- 扫码进设备→派单/接单/到场/解决/关闭  
- 费用（工时/差旅/备件，保内策略）；回访评分；知识库沉淀

11) 提成池与负数冲抵  
- 合同维度多参与人（销售/管理/服务/贡献）比例或固定额  
- 按回款节点自动应计；修订引发负数冲抵与调整审批

12) 移动与AI  
- 微信OA/小程序：我的待办、智能任务、附近客户、扫码查设备、审批、统计  
- 语音：录音→ASR→LLM抽取→MCP落库草案→卡片确认  
- AI：赢率预测、下一步建议、周/月经营Narrative、异常检测、知识库RAG

---

## 3. 功能模块说明

### 3.1 系统与字典
- 组织/用户/角色/权限管理、用户状态（入/离/调/禁用）
- 字典：地区、行业、客户类型/等级、税率（区间）、岗位/职级、商机阶段模板（多套）、产品目录（品类/系列/型号/规格/计量/税率/品牌）、价格策略（标准/协议/区域/促销；优先级+有效期）、合同模板/条款、付款方式/节点模板、服务类别与工时费率、发票类别、线索来源/跟进方式/赢输原因、售后问题分类、设备属性模板
- 能力：增删改、启停、排序、层级、导入导出、历史版本与引用影响分析
- 审批配置：商机转交、折扣审批、合同审批、采购审批、出差/报销审批

### 3.2 客户/公海/联系人/关系网
- 客户：名称唯一（名称+USCC+地区）、简称、USCC、行业、地区、地址、官网、规模、等级、来源、所属销售、协作人、上级/集团、重点/黑名单
- 联系人：唯一性（同客户下不重复），职位、部门、手机/座机、邮箱、IM、决策角色、生日、偏好、隐私同意
- 关系网络：母子公司、合作伙伴、竞争对手、同拓线索
- 跟进记录：拜访/电话/会议/纪要/附件；语音转写自动纪要
- 公海池：规则回收/认领；共享申请；销售交接批量加共享人

### 3.3 线索
- 录入：手工/导入/API/表单/OCR/语音
- 去重：手机号/邮箱/公司名+地区
- 分配：区域/来源/轮询/权重/余额；状态（新建/已分配/跟进中/已转化/无效）
- 转化：生成客户/联系人/商机/任务；幂等与来源链路
- 评分：LeadScore；转化率报表

### 3.4 商机/报价/看板
- 阶段模板（事业部差异化）与阶段出口校验
- 字段：预计金额（税前/税后）、概率、预计签约日、竞争对手、采购方式、决策链、关联产品/报价/项目
- 报价：多版本、版本比对、价税分离、折扣审批、打印模板
- 看板：按阶段拖拽、金额/概率汇总、WIP限制（可选）
- 预警：关键节点超期、久未推进、大额；停滞自动回收至公海（规则）

### 3.5 项目（实施/交付）
- 立项：来源（商机/合同/内部）、客户、项目经理、团队、预算、里程碑模板
- 任务/甘特：分解、负责人、计划/实际、依赖、工时登记
- 里程碑：方案确认、到货、安装调试、验收、培训、归档；触发回款计划
- 资料库：版本管理、水印、签章件归档
- 风险与问题：等级、负责人、处理时限、升级

### 3.6 产品/价格/库存（轻量）
- 产品：品类/系列/型号/规格参数、计量、税率、品牌、图片/附件、备件/耗材、捆绑/套餐
- 价格策略：标准/协议/区域/促销；优先级=协议>促销>区域>标准；有效期校验；价税换算一致
- 轻量库存：可售与在途；与采购/入库/出库单关联；变更日志

### 3.7 合同/订单/修订/回款/发票
- 合同：类型（直销/框架/代理/服务）、编号、客户、签约主体、币种/汇率、含/未税金额、税率、交付地址、付款方式、违约条款、项目经理、状态；产品明细；附件
- 审批：法务/财务/业务分级（金额/折扣阈值）
- 修订：修订单记录差异/原因/生效；最新有效视图；重算分期/提成/采购/出库/设备
- 订单与出库：合同派生订单；出库单打印；序列号贯通
- 回款：分期计划（模板生成可调）；到账登记（日期/金额/币种/方式/流水号），多对多核销；首逾/续逾催收与罚息
- 发票：开票申请/审批/登记（代码/号码/PDF）、红冲；与合同/订单/回款关联；税控接口预留

### 3.8 采购与供应商（轻量）
- 采购合同与销售合同N-N分摊；到货入库、质检与差异；入库允许出库/安装
- 供应商档案、评分、黑名单；采购审批

### 3.9 设备与售后
- 设备台账：品牌/型号/序列号/MAC/IMEI、安装位置、客户/项目、质保期、关联合同/出入库；二维码/条码
- 工单：来源（来电/设备告警/维保/合同）、类别、优先级、SLA、派单/受理、响应/到场/解决/关闭；费用（工时/差旅/备件，保内策略）
- 回访与维保：回访表与评分；维保合同（范围/响应时限/巡检计划/消耗件/续费提醒）
- 知识库：问题-原因-解决方案；复用率统计

### 3.10 办公与协同
- 待办与通知中心：我发起/我待办/抄送/已办；系统消息/邮件/短信/企业IM
- 日程与任务：个人/团队，关联客户/商机/项目；签到与附近客户
- 出差/报销：流程审批、附件影像；按权限批量操作
- 快递：顺丰接口回写到客户联系记录
- 周报/月报：选择汇报对象，支持批注与归档

### 3.11 流程/规则/通知
- 流程：可视化BPMN、条件路由、并行/会签、加签/转审、超时提醒、退回重提
- 规则：自动分配、自动回收、字段计算、跨对象联动（赢单→项目+回款计划）
- 通知：站内/邮件/短信/企业IM/微信模板消息；变量模板；频控/静默时段；重试与失败告警

### 3.12 报表与看板
- 标准报表：销售漏斗/转化/赢率/周期；客户新增/活跃/沉睡；商机/报价金额与折扣；合同/回款/开票趋势与同比环比；账龄与逾期；项目进度/工时；售后SLA/满意度；提成/奖励
- 自助分析：维度（时间/组织/人员/地区/行业/产品/客户等级/公海-私海），指标拖拽；订阅邮件；导出水印权限
- 看板：商机看板、售后SLA、项目里程碑、经理经营驾驶舱

### 3.13 移动端（微信OA/小程序）
- 登录与绑定：UnionID↔CRM用户；服务端JWT+RBAC
- 首页：我的待办、智能任务、快捷入口（新增客户/拜访、语音录入、附近客户、扫码查设备、审批、统计）
- 语音工作流：录音→转码→ASR→LLM抽取→MCP草案→卡片确认；低置信降级为表单草稿
- 离线与幂等：本地草稿缓存、失败重传、去重

### 3.14 AI智能中台
- Copilot：销售/技术/售后/经理角色摘要、建议、话术与提醒
- 预测与评分：LeadScore、Forecast（签约/回款）、风险预测（逾期/采购延迟/安装按期/客户流失/工单堆积）
- 异常检测：合同金额/毛利异常、修订频率、负数提成、阶段停滞
- 自动化周/月报：Narrative+图表；自然语言问答与下钻
- 知识库RAG：产品手册、安装SOP、FAQ；语义检索与引用标注
- 治理：模型注册/版本/灰度、置信门槛、人审回退、成本/延迟/采纳率监控、可解释性

### 3.15 财务：一次性奖励与提成池
- 一次性奖励：跨线/跨区转交审批通过后入账；固定或规则金额；台账与发放审批
- 提成池：多参与人比例/固定额；按回款节点自动应计；基于最新合同金额；负数冲抵；月度清单审计与调整审批

---

## 4. 数据模型（关键实体与字段）

设计原则：  
- 主键UUID，业务编号code；统一基础字段：status、owner_id、collaborators、org_unit_id、source、created_by/at、updated_by/at、remark、attachments  
- 金额decimal(18,2)；时间timestamptz；扩展jsonb custom_fields；软删+审计表；外键与唯一索引齐备

核心实体（节选关键字段）：  
- OrgUnit(id, name, parent_id, level, path)  
- User(id, name, mobile, email, org_unit_id, title, status)  
- Role/Permission/UserRole/RolePermission  
- Dictionary(id, type, code, name, parent_id, effective_from/to, status)  
- Customer(id, code, name, uscc, industry, region, address, website, level, source, owner_id, is_key, is_blacklist, parent_customer_id)  
- Contact(id, customer_id, name, title, dept, mobile, phone, email, im, role_tag, birthday, preference, consent)  
- Activity(id, customer_id, contact_id, project_id, type, subject, content, start_at, end_at, location, attachments)  
- Lead(id, source, channel, customer_name, contact_name, mobile, email, region, product_intent, summary, status, assigned_to)  
- Opportunity(id, customer_id, name, stage_template_id, stage, expected_amount_ex_tax, expected_amount_inc_tax, probability, expected_sign_at, competitor, purchase_method, decision_chain)  
- Quote(id, opportunity_id, current_version_id, print_template_id, approval_status)  
- QuoteVersion(id, quote_id, version_no, currency, tax_rate, total_ex_tax, total_inc_tax, discount_rate, note)  
- QuoteItem(id, quote_version_id, product_id, spec, uom, qty, unit_price_ex_tax, tax_rate, discount_rate)  
- Project(id, code, customer_id, source_type, manager_id, team_ids(jsonb), budget, status)  
- Milestone(id, project_id, name, planned_at, actual_at, status, triggers_receivable_plan(bool))  
- Task(id, project_id, name, assignee_id, planned_start/end, actual_start/end, dependency, status)  
- Timesheet(id, task_id, user_id, hours, cost_rate, date, note)  
- Product(id, code, name, brand, model, spec, uom, tax_rate, is_spare, is_bundle)  
- PricePolicy(id, type, region_id, customer_id, product_id, price_ex_tax, effective_from/to, priority)  
- SalesContract(id, code, customer_id, currency, fx_rate, tax_rate, amount_ex_tax, amount_inc_tax, signer_entity, delivery_address, payment_term_id, status)  
- ContractItem(id, sales_contract_id, product_id, spec, uom, qty, unit_price_ex_tax, tax_rate, discount_rate, delivery_due_at)  
- ContractRevision(id, sales_contract_id, rev_no, change_json, reason, effective_at, created_by)  
- Order(id, sales_contract_id, code, status, delivery_address, planned_ship_at)  
- OrderItem(id, order_id, contract_item_id, product_id, qty, status)  
- Delivery(id, order_id, code, shipped_at, carrier, doc_url)  
- DeliveryItem(id, delivery_id, order_item_id, serial_id, qty)  
- ReceivablePlan(id, sales_contract_id, term_name, due_at, amount, currency, status)  
- Receipt(id, code, received_at, amount, currency, method, bank_ref)  
- ReceiptApply(id, receipt_id, receivable_plan_id, applied_amount)  
- Invoice(id, code, type, title, tax_no, address_phone, bank_account, amount, tax_rate, status, sales_contract_id, order_id, receipt_id)  
- Supplier(id, name, uscc, region, contact, phone, rating, blacklist)  
- PurchaseContract(id, code, supplier_id, currency, amount, status)  
- SalesPurchaseMap(id, sales_contract_id, purchase_contract_id, ratio, amount)  
- Inbound(id, purchase_contract_id, code, received_at, inspector, status)  
- InboundItem(id, inbound_id, product_id, serial_id, qty, result)  
- Serial(id, code, product_id, batch_no, status)  
- Asset(id, code, product_id, serial_id, customer_id, project_id, installed_at, warranty_from/to, location)  
- ServiceOrder(id, code, asset_id, customer_id, type, priority, sla_id, status, reported_at, assigned_to, closed_at)  
- ServiceRecord(id, service_order_id, engineer_id, start_at, end_at, is_resolved, action, parts_cost, travel_cost, labor_hours, attachments)  
- ServiceSurvey(id, service_order_id, score, comment, survey_at)  
- WarrantyContract(id, customer_id, asset_id, scope, response_sla, inspection_plan, consumables(jsonb), expire_at)  
- BonusOnce(id, project_id, from_user_id, to_user_id, amount, status, approved_at)  
- CommissionPlan(id, sales_contract_id, participant(jsonb), rule_type, rule_json)  
- CommissionAccrual(id, sales_contract_id, period, stage, amount, is_negative, source_revision_id, status)  
- CommissionPayout(id, period, user_id, amount, status, approved_at)  
- CommissionAdjustment(id, accrual_id, delta, reason, approved_by)  
- Notification(id, channel, template_id, to_user_id, payload, status, retry)  
- WorkflowProcess(id, name, version, bpmn_ref)  
- WorkflowInstance(id, process_id, biz_type, biz_id, status)  
- AITask(id, type, subject, biz_ref, due_at, priority, suggestions(jsonb), status)  
- VoiceNote(id, user_id, audio_url, asr_text, llm_json, confidence, linked_entity, status)  
- Insight(id, type, scope, content_json, score, generated_at)  
- Forecast(id, scope, period, amount, method, metrics_json)  
- AnomalyEvent(id, type, severity, biz_ref, detected_at)  
- FileObject(id, biz_type, biz_id, filename, url, size, hash, watermark_info)

关系与约束（要点）：  
- Customer 1-N Contact/Activity/Opportunity/Project/SalesContract/Asset/ServiceOrder  
- Opportunity 1-N Quote；Quote 1-N QuoteVersion；QuoteVersion 1-N QuoteItem  
- Project N-N SalesContract（中间表 ProjectContract 或在合同存快照，实施选中间表）  
- SalesContract 1-N ContractItem/Order/ReceivablePlan；N-N PurchaseContract（SalesPurchaseMap）  
- Order 1-N OrderItem；Order 1-N Delivery；Delivery 1-N DeliveryItem；DeliveryItem 1-1 Serial  
- ReceivablePlan N-N Receipt（ReceiptApply）；Invoice ↔ Contract/Order/Receipt（可空外键）  
- PurchaseContract 1-N Inbound；Inbound 1-N InboundItem；InboundItem 0-1 Serial  
- Asset 由 ContractItem+Delivery/Install 生成；Asset 1-N ServiceOrder；ServiceOrder 1-N ServiceRecord、1-1 ServiceSurvey  
- CommissionPlan 1-N CommissionAccrual；Accrual N-1 Payout/Adjustment  
- 唯一索引：Customer(name,uscc,region)、Contact(customer_id,name,mobile?)、Serial(code)

编码与编号规则：  
- 客户编号17位：YYMMDD + 当日顺序4 + 国家2 + 区域2 + 来源3（并发取号/唯一/格式校验）  
- 合同/订单/出入库/工单/发票：前缀+年月+序号，支持分段

索引与性能：  
- 常用索引：owner_id、org_unit_id、status、stage、expected_sign_at、due_at、customer_id、sales_contract_id、asset_id  
- 全文检索：客户/联系人/商机/纪要（支持拼音/首字母）  
- 审计表分区（月）

---

## 5. 技术选型与架构

- 前端：
  - 框架: Vue 3 + TypeScript
  - 构建工具: Vite
  - UI组件库: Element Plus
  - CSS框架: TailwindCSS
  - 状态管理: Pinia
  - 路由: Vue Router 4
  - HTTP客户端: Axios
  - 图表库: ECharts
  - 微信或钉钉小程序（uni-app）
- 后端：
  - Spring Boot；
  - 工作流（Camunda 7/bpmn-js）
  - 规则引擎（json-rules DSL）  
- 数据库：
  - PostgreSQL；搜索（内置全文）；
  - 缓存（阶段性）：进程内缓存（LRU/TTL）+ 物化视图；
  - 异步/定时：pg-boss（基于PostgreSQL）  
  - 文件：MinIO(S3) + 预签名；PDF打印：Puppeteer  
  - 认证：OIDC/SSO；微信UnionID绑定；2FA可选  
  - 观测：OpenTelemetry、Prometheus、Grafana；日志：EFK  
  - 安全：OWASP Top 10、字段脱敏、IP白名单、RBAC+ABAC、TLS与静态加密  
- AI与语音：ASR（腾讯云/讯飞），LLM（腾讯混元优先，路由通义/Claude 备选），Embedding（bge-m3），向量库（pgvector）

架构要点：  
- API网关 → 业务服务（销售/合同/采购/售后/财务/报表/AI）→ 工作流与规则 → 通知网关 → 文件与搜索  
- 多租户与数据分权：org_unit隔离；后续可升级schema级  
- 幂等：导入、语音回传、审批回调；重试与补偿  
- 阶段性选型策略（M1–M3）：不引入Redis/RabbitMQ；使用pg-boss（基于PostgreSQL）处理异步/定时；热点读采用进程内缓存+ETag/HTTP缓存，聚合场景采用物化视图；满足多实例会话/限流/分布式锁、复杂延时/死信/路由、异步量显著增长等条件再评估引入。  
- 微信语音链路：媒体下载→转码→ASR→LLM抽取→校验→草案→用户确认→落库；全链路审计与回滚

集成：  
- ERP：产品/价格/库存同步，应收对账（幂等）  
- 税控/电子发票：开票与红冲占位  
- 地图/企业信息：地理编码、工商查询  
- 快递：顺丰接口
- 企查查：企业信息、工商查询

---

## 6. 开发路线与计划（建议）

阶段选型策略：M1–M3不引入Redis/RabbitMQ，采用PostgreSQL为中心：PG索引+物化视图支撑查询与聚合；pg-boss（基于PG）承载异步/定时；进程内缓存（LRU/TTL）+ ETag/HTTP缓存；M4+依据会话/限流/分布式锁、复杂延时/死信/路由、异步量增长等触发条件再评估引入。

- M1（4-6周）：组织/角色/权限、字典、公海/私海、客户/联系人、线索、分配与去重、全局检索（基础）  
  - 验收：权限口径、去重/合并、公海回收/认领、导入导出水印日志

- M2（4-6周）：商机/阶段模板/出口校验、报价多版本与折扣审批、看板与预警  
  - 验收：阶段强校验、报价版本比对、看板拖拽权限、预警触达

- M3（4-6周）：合同/订单/产品明细、分期计划、订单与出库、打印模板、导入导出  
  - 验收：含/未税口径一致、审批路由、打印正确

- M4（4-6周）：合同修订与重算、采购N-N、入库、安装/验收、设备台账  
  - 验收：修订差异与最新视图、采购分摊、序列号贯通

- M5（4-6周）：回款/核销/逾期催收、发票申请/登记/红冲、账龄报表  
  - 验收：多对多核销、首逾/续逾、发票状态机

- M6（4-6周）：售后工单/SLA/费用、回访/维保、知识库、报表中心与自助分析v1  
  - 验收：SLA计时与升级、知识库复用率、报表订阅

- M7（6-8周，移动+AI）：微信OA/小程序、语音链路（ASR+LLM+MCP）、智能任务、Copilot v1、LeadScore/Forecast、异常检测、周/月Narrative  
  - 验收：语音录入<8s回传、预测/建议可解释、采纳率指标

发布与质量保障：  
- 环境：Dev/Test/Stage/Prod；蓝绿/金丝雀  
- CI/CD：代码扫描、单测/集成、DB迁移、契约/E2E（关键流程）  
- 性能：核心列表P95<1.5s，报表异步；容量规划与压测  
- 安全：渗透与越权测试；导出水印与日志抽查

---

## 7. 非功能性要求

- 安全：RBAC+ABAC、字段脱敏、IP白名单、2FA、TLS、静态加密、附件病毒扫描、下载水印+用途  
- 性能：移动首屏TTI≤1.5s；列表100条≤1.5s；语音链路端到端≤8s；周/月报≤5分钟；在线问答P95≤2s  
- 可用性：月SLA≥99.9%；熔断与降级；消息与任务失败重试  
- 审计/合规：操作与数据留痕；GDPR/隐私同意；语音留存周期（默认90/180天可配）  
- 备份与容灾：RPO≤15min，RTO≤2h；演练  
- 多语言与多币种：中文为主；币种换算（汇率快照）

---

## 8. 报表与导出

- 销售：活动、漏斗、成交、合同销售、排行、年度对比、业绩地图、自定义  
- 采购/交付：采购执行、到货/入库、安装/验收  
- 财务：回款率、逾期余额、账龄、现金流预测  
- 提成/奖励：项目与人员清单、负数冲抵明细  
- 移动/AI：移动活跃、ASR成功率、AI采纳率/延迟/成本、人工回退率  
- 导出：客户/联系人/行动/项目/合同/产品/回款/开票/出库/采购；遵循可见范围与水印审计

---

## 9. 接口与协议（概要）

- 风格：REST v1 + OpenAPI；分页/过滤/排序；幂等键Idempotency-Key；Webhook（创建/变更/审批）  
- 认证：OIDC/Token；微信UnionID绑定；API层权限校验  
- 典型端点：  
  - /customers、/customers/{id}/merge  
  - /leads/import、/leads/assign、/leads/{id}/convert  
  - /opportunities/{id}/advance、/quotes/{id}/versions  
  - /contracts、/contracts/{id}/revisions、/orders、/deliveries  
  - /receivable-plans、/receipts/apply  
  - /invoices、/invoices/{id}/red  
  - /purchase-contracts、/inbounds  
  - /assets、/service-orders  
  - /commissions/plans、/commissions/accruals  
  - /ai/insights、/ai/forecast、/ai/anomalies、/wechat/media/callback

---

## 10. 关键业务规则

- 去重：客户=名称+USCC+地区；线索=手机号/邮箱/公司+地区；重复提示与合并建议  
- 价格优先级：协议>促销>区域>标准；有效期校验；价税换算一致  
- 折扣审批：分级阈值（行/单/总额）；越权禁止；全链路留痕  
- 逾期：首逾T+X催收，续逾Y升级；罚息规则表；任务与通知联动  
- SLA：类别/优先级×工作日历×响应/解决时限；超时预警与升级  
- 导出：需权限；水印（用户名/时间/IP/用途）；日志与抽查  
- 修订：仅修订单可改；最新视图+历史差异页；触发重算幂等  
- 提成：以“最新合同金额”按回款节点应计；允许负数冲抵；调整需审批

---

## 11. 验收清单（节选）

- 权限：范围×动作×字段生效；共享只读或受限编辑；导出水印与日志  
- 公海/私海：认领/分配/回收/限额；离职自动释放  
- 主流程：线索→商机→报价→合同/修订→订单/出库→安装/验收→回款/发票→设备→售后  
- 审批：折扣/合同/采购/报销/转交/调整可走通；超时提醒  
- 核销：Receipt↔ReceivablePlan多对多；余额一致；账龄口径统一  
- 修订：最新视图一致、差异可读；分期/提成重算正确  
- 提成/奖励：规则计算、负数冲抵、调整审批与审计  
- 报表：漏斗/趋势/账龄/提成/维保准确；自助分析权限受控  
- 移动/AI：语音录入<8s回传；Narrative按周/月推送；异常检测触发与到达

---

## 12. 数据迁移

- 范围：客户/联系人/活动/线索/商机/报价/合同/合同产品/回款/发票/设备/工单  
- 工具：模板、字段映射、枚举对齐、错误回写；断点续传；唯一键与去重  
- 验收：抽样核对≥99.5%字段准确；金额口径一致；历史版本保留策略

---

## 13. 风险与待确认

- 私海边界：仅直属上级还是全链路上级可见  
- 转交确认口径与奖励规则；撤销/冲销策略  
- 提成口径：含/未税、参与人范围、回款节点、负数结转  
- 修订类型对回款/采购/出库/设备的影响矩阵细化  
- 采购分摊算法与利润分析口径  
- 微信与ASR费用与限额、失败退化与人审队列  
- AI治理：模型选择、成本配额、可解释、数据漂移  
- 数据留存：语音与转写留存周期、撤回SLA

---

## 14. 附：页面与交互（关键清单）

- 全局：首页仪表、待办中心、全局检索、设置（字典/流程/权限/通知模板）  
- 客户：列表（筛选/分组/导出）、详情（概览、联系人、跟进、商机、合同、设备、工单、文件）、合并、公海认领  
- 线索：列表/看板、分配、批量导入、转化向导  
- 商机：看板拖拽、阶段校验、产品与报价版本、活动纪要、预警  
- 项目：列表、甘特、里程碑、任务/工时、资料库  
- 合同/订单：合同编辑（条款库、审批）、订单派生、产品明细、附件  
- 回款/发票：计划与登记、核销、逾期催办、开票申请/登记/红冲  
- 售后：设备台账（扫码）、工单流转与费用、回访与评价、知识库  
- 报表：标准集、自助分析、订阅与导出  
- 移动端：我的、待办、附近客户、扫码、语音录入、审批、统计

