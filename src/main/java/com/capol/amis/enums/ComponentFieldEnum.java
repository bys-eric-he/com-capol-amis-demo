package com.capol.amis.enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.Optional;

/**
 * 组件对应的数据库字段类型
 */
@Getter
public enum ComponentFieldEnum {

    /**
     * 表单项组件
     */
    TEXT("表格文本框", "text", 1, 1, "text", 0),
    INPUT_TEXT("文本框", "input-text", 1, 1, "text", 0),
    INPUT_EMAIL("邮箱", "input-email", 1, 1, "text", 0),
    TEXTAREA("多行文本框", "textarea", 1, 1, "text", 0),
    SELECT("下拉框", "select", 2, 1, "text", 0),
    CHECKBOXES("复选框", "radios", 2, 1, "text", 0),
    RADIOS("单选框", "text", 2, 1, "text", 0),
    INPUT_DATE("日期框", "input-date", 3, 1, "varchar", 40),
    INPUT_MONTH("Month", "input-month", 3, 1, "varchar", 40),
    INPUT_QUARTER("Quarter", "input-quarter", 3, 1, "varchar", 40),
    INPUT_YEAR("Year", "input-year", 3, 1, "varchar", 40),
    INPUT_DATE_RANGE("日期范围", "input-date-range", 3, 1, "varchar", 40),
    INPUT_DATETIME_RANGE("日期时间范围", "input-datetime-range", 3, 1, "varchar", 40),
    INPUT_MONTH_RANGE("月份范围", "input-month-range", 3, 1, "varchar", 40),
    INPUT_QUARTER_RANGE("季度范围", "input-quarter-range", 3, 1, "varchar", 40),
    INPUT_DATETIME("日期时间", "input-datetime", 3, 1, "varchar", 40),
    CHECKBOX("勾选框", "checkbox", 1, 1, "varchar", 40),
    INPUT_CITY("城市选择", "input-city", 3, 2, "varchar", 40),
    INPUT_RATING("评分", "input-rating", 1, 1, "tinyint", 2),
    PICKER("项目台账", "picker", 3, 2, "text", 0),
    TREE_SELECT("组织架构", "tree-select", 3, 1, "text", 0);

    /**
     * 组件名称
     */
    private String name;

    /**
     * 组件类型值
     */
    private String value;

    /**
     * 分组1.文本单字段类 2.选项下拉类 3.双字段类
     */
    private Integer group;

    /**
     * 生成字段数量
     */
    private Integer count;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    ComponentFieldEnum(String name, String value, Integer group, Integer count, String fieldType, Integer fieldLength) {
        this.name = name;
        this.value = value;
        this.group = group;
        this.count = count;
        this.fieldType = fieldType;
        this.fieldLength = fieldLength;
    }

    /**
     * 根据组件类型获取枚举
     *
     * @param type
     * @return
     */
    public static ComponentFieldEnum getEnumByType(String type) {
        Optional<ComponentFieldEnum> enumOptional = Arrays.stream(ComponentFieldEnum.values())
                .filter(componentFieldEnum -> componentFieldEnum.getValue().equals(type)).findFirst();

        if (enumOptional.isPresent()) {
            ComponentFieldEnum fieldEnum = enumOptional.get();
            return fieldEnum;
        }

        return null;
    }
}
