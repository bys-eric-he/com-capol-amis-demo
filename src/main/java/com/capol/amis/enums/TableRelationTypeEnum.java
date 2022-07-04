package com.capol.amis.enums;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/1 15:32
 * desc: 表关系类型
 */
public enum TableRelationTypeEnum {
    /**
     * 表关系类型
     */
    MAIN_TYPE("主表", 1),
    SUB_TYPE("从表", 2);

    private final String name;
    private final Integer value;


    TableRelationTypeEnum(String name, Integer value) {
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
