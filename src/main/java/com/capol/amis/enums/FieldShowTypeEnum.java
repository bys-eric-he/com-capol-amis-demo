package com.capol.amis.enums;

/**
 * 字段显示类型(1、显示 2、隐藏)
 */
public enum FieldShowTypeEnum {

    SHOW("显示", 1),
    HIDE("隐藏", 2);

    private String name;
    private Integer value;

    FieldShowTypeEnum(String name, Integer value) {
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
