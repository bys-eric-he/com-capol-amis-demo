package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.amis.entity.base.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 业务主题列表配置表
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_template_grid_conf")
public class TemplateGridConfDO extends BaseInfo {
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
     * 数据主表ID
     */
    private Long formTableId;

    /**
     * 数据从表ID
     */
    private Long gridTableId;

    /**
     * 数据从表名称
     */
    private String gridTableName;

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
     * 字段顺序
     */
    private Integer fieldOrder;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 组件类型
     */
    private String componentType;

}
