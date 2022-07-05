package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 表单字段配置表
 * cfg_form_field_config
 */
@TableName(value ="cfg_form_field_config")
@Data
public class FormFieldConfigDO implements Serializable {
    /**
     * 主键Id
     */
    @TableId
    private Long id;

    /**
     * 业务主题Id(业务主题表的Id)
     */
    private Long subjectId;

    /**
     * 表名注释
     */
    private String tableAlias;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表名标识
     */
    private String tableKey;

    /**
     * 字段名注释
     */
    private String fieldAlias;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段名标识
     */
    private String fieldKey;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 组件类型
     */
    private String componentType;

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
     * 表关系类型(1主表;2从表)
     */
    private Integer tableRelationType;

    /**
     * 序号
     */
    private Long orderNo;

    /**
     * 状态 1:正常，0:删除
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建主机ID
     */
    private String createdHostIp;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后创建人
     */
    private String lastOperator;

    /**
     * 最后修改主机ID
     */
    private String updateHostIp;

    /**
     * 最后创建人id
     */
    private Long lastOperatorId;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}