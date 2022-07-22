package com.capol.amis.enums;

/**
 * 字段数据类型
 * 1、单一值 2、原始值 3、转换值
 */
public enum FieldDataTypeEnum {

    SINGLE("单一值", 1),
    ORIGINAL("原始值", 2),
    TRANSFORM("转换值", 3);

    private String name;
    private Integer value;

    FieldDataTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
