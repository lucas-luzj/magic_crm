-- =============================================
-- 产品管理相关表
-- =============================================

-- 1. 产品分类表
CREATE TABLE IF NOT EXISTS product_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL, -- 分类编码
    name VARCHAR(100) NOT NULL, -- 分类名称
    parent_id UUID, -- 父分类ID
    level INTEGER DEFAULT 1, -- 层级
    path VARCHAR(500), -- 路径
    sort_order INTEGER DEFAULT 0, -- 排序
    icon VARCHAR(100), -- 图标
    description TEXT, -- 描述
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES product_categories(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_product_categories_code ON product_categories(code);
CREATE INDEX idx_product_categories_name ON product_categories(name);
CREATE INDEX idx_product_categories_parent_id ON product_categories(parent_id);
CREATE INDEX idx_product_categories_path ON product_categories(path);
CREATE INDEX idx_product_categories_deleted ON product_categories(deleted);

-- 2. 产品表
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL, -- 产品编码
    name VARCHAR(200) NOT NULL, -- 产品名称
    category_id UUID, -- 产品分类
    brand VARCHAR(100), -- 品牌
    model VARCHAR(100), -- 型号
    spec TEXT, -- 规格参数
    unit_of_measure VARCHAR(20), -- 计量单位
    tax_rate DECIMAL(5,2), -- 税率
    standard_price DECIMAL(18,2), -- 标准价格
    cost_price DECIMAL(18,2), -- 成本价格
    min_price DECIMAL(18,2), -- 最低价格
    is_spare_part BOOLEAN DEFAULT false, -- 是否备件
    is_bundle BOOLEAN DEFAULT false, -- 是否套餐
    is_customizable BOOLEAN DEFAULT false, -- 是否可定制
    lead_time INTEGER, -- 交付周期(天)
    warranty_period INTEGER, -- 质保期(月)
    images JSONB, -- 产品图片
    attachments JSONB, -- 附件
    description TEXT, -- 产品描述
    features TEXT, -- 产品特性
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 状态: ACTIVE, INACTIVE, DISCONTINUED
    custom_fields JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES product_categories(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_products_code ON products(code);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_model ON products(model);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_deleted ON products(deleted);

-- 3. 价格策略表
CREATE TABLE IF NOT EXISTS price_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    policy_type VARCHAR(50) NOT NULL, -- 策略类型: STANDARD, AGREEMENT, REGIONAL, PROMOTION
    policy_name VARCHAR(100) NOT NULL, -- 策略名称
    product_id UUID, -- 产品ID
    customer_id UUID, -- 客户ID(协议价)
    region VARCHAR(100), -- 地区(区域价)
    price_ex_tax DECIMAL(18,2), -- 不含税价格
    price_inc_tax DECIMAL(18,2), -- 含税价格
    discount_rate DECIMAL(5,2), -- 折扣率
    min_quantity INTEGER, -- 最小数量
    max_quantity INTEGER, -- 最大数量
    currency VARCHAR(10) DEFAULT 'CNY', -- 币种
    effective_from DATE, -- 生效开始日期
    effective_to DATE, -- 生效结束日期
    priority INTEGER DEFAULT 0, -- 优先级
    approval_status VARCHAR(20) DEFAULT 'APPROVED', -- 审批状态
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark TEXT,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_price_policies_policy_type ON price_policies(policy_type);
CREATE INDEX idx_price_policies_product_id ON price_policies(product_id);
CREATE INDEX idx_price_policies_customer_id ON price_policies(customer_id);
CREATE INDEX idx_price_policies_region ON price_policies(region);
CREATE INDEX idx_price_policies_effective_dates ON price_policies(effective_from, effective_to);
CREATE INDEX idx_price_policies_priority ON price_policies(priority);
CREATE INDEX idx_price_policies_deleted ON price_policies(deleted);

-- 4. 产品套餐关系表
CREATE TABLE IF NOT EXISTS product_bundles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bundle_product_id UUID NOT NULL, -- 套餐产品ID
    component_product_id UUID NOT NULL, -- 组件产品ID
    quantity INTEGER NOT NULL DEFAULT 1, -- 数量
    is_optional BOOLEAN DEFAULT false, -- 是否可选
    sort_order INTEGER DEFAULT 0, -- 排序
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_bundle_product FOREIGN KEY (bundle_product_id) REFERENCES products(id),
    CONSTRAINT fk_component_product FOREIGN KEY (component_product_id) REFERENCES products(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_product_bundles_bundle_product_id ON product_bundles(bundle_product_id);
CREATE INDEX idx_product_bundles_component_product_id ON product_bundles(component_product_id);
CREATE INDEX idx_product_bundles_deleted ON product_bundles(deleted);

-- 5. 轻量库存表
CREATE TABLE IF NOT EXISTS inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL, -- 产品ID
    warehouse_code VARCHAR(50) DEFAULT 'MAIN', -- 仓库编码
    available_quantity INTEGER DEFAULT 0, -- 可用库存
    in_transit_quantity INTEGER DEFAULT 0, -- 在途库存
    reserved_quantity INTEGER DEFAULT 0, -- 预留库存
    total_quantity INTEGER DEFAULT 0, -- 总库存
    min_stock_level INTEGER, -- 最小库存
    max_stock_level INTEGER, -- 最大库存
    reorder_point INTEGER, -- 补货点
    last_inbound_date TIMESTAMP, -- 最后入库时间
    last_outbound_date TIMESTAMP, -- 最后出库时间
    status VARCHAR(20) DEFAULT 'NORMAL', -- 状态: NORMAL, LOW, OUT_OF_STOCK
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
CREATE INDEX idx_inventory_warehouse_code ON inventory(warehouse_code);
CREATE INDEX idx_inventory_status ON inventory(status);
CREATE UNIQUE INDEX idx_inventory_unique ON inventory(product_id, warehouse_code);

-- 6. 库存变更日志表
CREATE TABLE IF NOT EXISTS inventory_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL, -- 产品ID
    warehouse_code VARCHAR(50) DEFAULT 'MAIN', -- 仓库编码
    change_type VARCHAR(50) NOT NULL, -- 变更类型: INBOUND, OUTBOUND, ADJUST, RESERVE, RELEASE
    reference_type VARCHAR(50), -- 关联类型: PURCHASE, SALES, ADJUSTMENT
    reference_id UUID, -- 关联ID
    quantity_before INTEGER, -- 变更前数量
    quantity_change INTEGER, -- 变更数量
    quantity_after INTEGER, -- 变更后数量
    reason TEXT, -- 变更原因
    operator_id UUID, -- 操作人
    operated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 操作时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_operator FOREIGN KEY (operator_id) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_inventory_logs_product_id ON inventory_logs(product_id);
CREATE INDEX idx_inventory_logs_warehouse_code ON inventory_logs(warehouse_code);
CREATE INDEX idx_inventory_logs_change_type ON inventory_logs(change_type);
CREATE INDEX idx_inventory_logs_reference ON inventory_logs(reference_type, reference_id);
CREATE INDEX idx_inventory_logs_operated_at ON inventory_logs(operated_at);

-- 添加注释
COMMENT ON TABLE product_categories IS '产品分类表';
COMMENT ON TABLE products IS '产品表';
COMMENT ON TABLE price_policies IS '价格策略表';
COMMENT ON TABLE product_bundles IS '产品套餐关系表';
COMMENT ON TABLE inventory IS '轻量库存表';
COMMENT ON TABLE inventory_logs IS '库存变更日志表';