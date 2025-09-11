package com.magic.crm.dto;

import java.util.Map;

import lombok.Data;

@Data
public class PageAndSearchRequest {
    private int pageIndex;
    private int pageSize;
    private Map<String, Object> FieldConditionArray;
}
