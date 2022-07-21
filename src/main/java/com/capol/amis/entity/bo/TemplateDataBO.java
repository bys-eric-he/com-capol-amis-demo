package com.capol.amis.entity.bo;

import lombok.Data;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/12 17:27
 * desc: 通用模板数据BO
 */
@Data
public class TemplateDataBO {
    /**
     * 数据行ID
     */
    private Long rowId;
    /**
     * 表单配置ID
     */
    private Long templateId;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 业务主题ID
     */
    private Long subjectId;

    /**
     * 字段标识
     */
    private String fieldKey;

    /**
     * 字段别名
     */
    private String fieldAlias;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字符串值
     */
    private String fieldStringValue;

    /**
     * 数字值
     */
    private Long fieldNumberValue;

    /**
     * 描述性值
     */
    private String fieldTextValue;

    /**
     * 值hash
     */
    private Long fieldHashValue;
}
