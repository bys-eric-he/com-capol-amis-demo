package com.capol.amis.entity.bo;

import com.capol.amis.enums.TableFieldTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:41
 * desc: 数据集字段基本信息, 用于表征
 */
@Data
public class DatasetFieldBasicBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据集主键id
     */
    private Long datasetId;

    /**
     * 主题表id
     */
    private Long tableId;

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
     * 原始字段类型 {@link com.capol.amis.enums.TableFieldTypeEnum}
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

}
