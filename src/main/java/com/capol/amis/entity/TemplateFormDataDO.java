package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.amis.entity.base.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 业务主题表单数据表
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_template_form_data")
public class TemplateFormDataDO extends BaseInfo {
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

    /**
     * 1：正常，0：已删除
     */
    private Integer status;
}
