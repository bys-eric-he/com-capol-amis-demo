package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数据集字段表
 * @TableName t_dataset_field
 */
@TableName(value ="t_dataset_field_324225698253233659")
@Data
public class DatasetFieldDO implements Serializable {
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

    /**
     * 状态 1:正常，0:删除
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人IP
     */
    private String createdHostIp;

    /**
     * 最后操作人
     */
    private String lastOperator;

    /**
     * 最后操作人id
     */
    private Long lastOperatorId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人IP
     */
    private String updateHostIp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}