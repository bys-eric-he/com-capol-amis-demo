package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.amis.entity.base.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <p>
 * 表单字典表
 * </p>
 *
 * @author He.Yong
 * @since 2022-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cfg_form_dict")
public class CfgFormDictDO extends BaseInfo {
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