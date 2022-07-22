package com.capol.amis.enums;

/**
 * 字段来源类型
 * 1、系统 2、自定义
 */
public enum FieldSourceTypeEnum {

    SYSTEM("系统", 1),
    SELF_DEFINED("自定义", 2);

    private String name;
    private Integer value;

    FieldSourceTypeEnum(String name, Integer value) {
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
