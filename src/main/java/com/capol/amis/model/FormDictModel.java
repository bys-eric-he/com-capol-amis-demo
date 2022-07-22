package com.capol.amis.model;

import lombok.Data;

/**
 * 表单字典
 */
@Data
public class FormDictModel {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 序号
     */
    private Integer orderNo;
}
