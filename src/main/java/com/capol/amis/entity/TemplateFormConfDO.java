package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.amis.entity.base.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 业务主题表单配置表
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_template_form_conf")
public class TemplateFormConfDO extends BaseInfo {
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
     * 数据表ID
     */
    private Long tableId;

    /**
     * 数据表名称
     */
    private String tableName;

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
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 字段数据类型(1单一值;2原始值;3转换值)
     */
    private Integer fieldDataType;

    /**
     * 字段显示类型(1显示;2隐藏)
     */
    private Integer fieldShowType;

    /**
     * 字段来源类型(1系统;2自定义)
     */
    private Integer fieldSourceType;

    /**
     * 字段顺序
     */
    private Integer fieldOrder;

    /**
     * 组件类型
     */
    private String componentType;
}
