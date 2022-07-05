package com.capol.amis.enums;

import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/1 15:28
 * desc: 表格字段类型枚举
 */
public enum TableFieldTypeEnum {
    BIGINT(1, "bigint"),
    TINYINT(2, "tinyint"),
    VARCHAR(3, "varchar"),
    DATETIME(4, "datetime"),
    TEXT(5, "text");

    private final Integer typeCode;
    private final String typeDesc;

    TableFieldTypeEnum(Integer typeCode, String typeDesc) {
        this.typeCode = typeCode;
        this.typeDesc = typeDesc;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    /**
     * 依据表格字段类型名称获取枚举
     */
    public static TableFieldTypeEnum getFieldTypeEnumByDesc(String desc) {
        return Stream.of(values()).filter(fieldType -> fieldType.getTypeDesc().equals(desc)).findFirst().orElse(null);
    }
}
