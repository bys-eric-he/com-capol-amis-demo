package com.capol.amis.enums;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/1 11:24
 * desc: 表来源类型枚举
 */
public enum TableSourceTypeEnum {
    WIDE_TABLE(1, "宽表"),
    USER_DIFINE_TABLE(2, "用户自定义表");

    private Integer code;
    private String desc;

    TableSourceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
