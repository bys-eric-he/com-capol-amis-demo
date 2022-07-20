package com.capol.amis.enums;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/6 10:55
 * desc: 数据源枚举
 */
public enum DBTypeEnum {
    AMIS_DEMO("amisDemo"),
    QA_BIZ("qaBiz"),
    REPORT("report");

    private final String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
