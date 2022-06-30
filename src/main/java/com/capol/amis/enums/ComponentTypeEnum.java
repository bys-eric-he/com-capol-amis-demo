package com.capol.amis.enums;

/**
 * 解析用到的组件枚举类型
 */
public enum ComponentTypeEnum {
    /**
     * 表单项组件
     */
    TEXT("文本框", "text", 1),
    INPUT_TEXT("文本框", "input-text", 1),
    INPUT_EMAIL("企业报表", "input-email", 1),
    TEXTAREA("多行文本框", "textarea", 1),
    SELECT("下拉框", "select", 1),
    CHECKBOXES("复选框", "checkboxes", 1),
    RADIOS("单选框", "radios", 1),
    INPUT_DATE("日期框", "input-date", 1),
    INPUT_MONTH("Month", "input-month", 1),
    INPUT_QUARTER("Quarter", "input-quarter", 1),
    INPUT_YEAR("Year", "input-year", 1),
    INPUT_DATE_RANGE("日期范围", "input-date-range", 1),
    INPUT_DATETIME_RANGE("日期时间范围", "input-datetime-range", 1),
    INPUT_MONTH_RANGE("月份范围", "input-month-range", 1),
    INPUT_QUARTER_RANGE("季度范围", "input-quarter-range", 1),
    INPUT_DATETIME("日期时间", "input-datetime", 1),
    CHECKBOX("勾选框", "checkbox", 1),
    INPUT_CITY("城市选择", "input-city", 1),
    INPUT_RATING("评分", "input-rating", 1),

    /**
     * 容器组件
     */
    FORM("表单", "form", 0),
    INPUT_TABLE("表格编辑框", "input-table", 2),
    TABS("选项卡", "tabs", 3),
    GRID("栏", "grid", 3),

    /**
     * 关联组件
     */
    PICKER("项目台账", "picker", 1),
    TREE_SELECT("组织架构组件", "tree-select", 1);

    /**
     * 组件名称
     */
    private String name;

    /**
     * 组件类型值
     */
    private String value;

    /**
     * 解析组件分支的分类
     * <p>
     * 0表单
     * <p>
     * 1表单项组件
     * <p>
     * 2表格编辑框
     * <p>
     * 3三栏，两栏，选项卡
     */
    private Integer type;

    ComponentTypeEnum(String name, String value, Integer type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;

    }
}
