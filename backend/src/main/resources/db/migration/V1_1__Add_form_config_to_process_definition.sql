-- 为流程定义表添加表单配置字段（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'crm_process_definition' 
                   AND column_name = 'form_config') THEN
        ALTER TABLE crm_process_definition ADD COLUMN form_config TEXT;
    END IF;
END $$;

-- 添加字段注释
COMMENT ON COLUMN crm_process_definition.form_config IS '表单配置数据(JSON格式)';

-- 添加索引以提高查询性能（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes 
                   WHERE indexname = 'idx_process_definition_form_key') THEN
        CREATE INDEX idx_process_definition_form_key ON crm_process_definition USING btree (substring(form_config, 1, 100));
    END IF;
END $$;