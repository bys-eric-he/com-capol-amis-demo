package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.amis.entity.base.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据集字段表
 * @TableName t_dataset_field
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="t_dataset_field")
@Data
public class DatasetFieldDO extends BaseInfo {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键Id
     */
    @TableId
    private Long id;

    /**
     * 数据集主键id
     */
    private Long datasetId;

    /**
     * 业务主题key
     */
    private String subjectKey;

    /**
     * 业务主题Id
     */
    private Long subjectId;

    /**
     * 主题表id
     */
    private Long tableId;

    /**
     * 主表名
     */
    private String tableName;

    /**
     * 表别称(给报表制作使用),规则是T拼最大下标
     */
    private String tableNameAlias;

    /**
     * 表来源类型(1宽表;2用户自定义表)
     */
    private Integer tableSourceType;

    /**
     * 字段主键id
     */
    private Long fieldId;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段别名(用户定义)
     */
    private String fieldAlias;

    /**
     * 字段别称(给报表制作使用),规则是F拼最大下标
     */
    private String fieldNameAlias;

    /**
     * 字段类型(1文本;2数值;3日期)(用户定义)
     */
    private Integer fieldTypeNum;

    /**
     * 显示类型(1显示;0不显示)
     */
    private Integer showType;

    /**
     * 原始字段类型
     */
    private String oriFieldType;

    /**
     * 序号
     */
    private Integer orderNo;

}