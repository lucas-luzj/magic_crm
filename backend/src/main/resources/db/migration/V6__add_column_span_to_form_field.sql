-- 为form_field表添加column_span字段
ALTER TABLE form_field ADD COLUMN IF NOT EXISTS column_span INTEGER DEFAULT 24;

-- 更新现有数据，设置默认值为24（整行宽度）
UPDATE form_field SET column_span = 24 WHERE column_span IS NULL;

-- 添加注释
COMMENT ON COLUMN form_field.column_span IS '字段栏位宽度，24为整行，12为半行，8为1/3行等';
