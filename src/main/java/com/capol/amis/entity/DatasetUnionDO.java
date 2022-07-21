package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数据集关联表
 * @TableName t_dataset_union_324225698253233659
 */
@TableName(value ="t_dataset_union_324225698253233659")
@Data
public class DatasetUnionDO implements Serializable {
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
     * 来源业务主题key
     */
    private String sourceSubjectKey;

    /**
     * 来源业务主题Id
     */
    private Long sourceSubjectId;

    /**
     * 来源表Id
     */
    private Long sourceTableId;

    /**
     * 来源表名
     */
    private String sourceTableName;

    /**
     * 来源字段主键id
     */
    private Long sourceFieldId;

    /**
     * 目标业务主题key
     */
    private String targetSubjectKey;

    /**
     * 目标业务主题Id
     */
    private Long targetSubjectId;

    /**
     * 目标表id
     */
    private Long targetTableId;

    /**
     * 目标表名
     */
    private String targetTableName;

    /**
     * 目标字段主键id
     */
    private Long targetFieldId;

    /**
     * 关联类型(1左关联)
     */
    private Integer unionType;

    /**
     * 目标表别称(给报表制作使用),规则是T拼最大下标
     */
    private String targetTableNameAlias;

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