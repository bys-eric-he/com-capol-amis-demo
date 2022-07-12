package com.capol.amis.model.result;

import com.capol.amis.model.result.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 业务主题表单数据
 */
@ApiModel(value = "业务主题表单数据")
@Data
public class FormDataInfoModel extends BaseModel {
    /**
     * 数据行ID
     */
    @ApiModelProperty(value = "数据行ID")
    private Long rowId;
    /**
     * 表单配置ID
     */
    @ApiModelProperty(value = "表单配置ID")
    private Long templateId;

    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID")
    private Long enterpriseId;

    /**
     * 项目ID
     */
    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    /**
     * 业务主题ID
     */
    @ApiModelProperty(value = "业务主题ID")
    private Long subjectId;

    /**
     * 数据表ID
     */
    @ApiModelProperty(value = "数据表ID")
    private Long tableId;

    /**
     * 列表名称
     */
    @ApiModelProperty(value = "列表名称")
    private String tableName;

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    private String fieldKey;

    /**
     * 字段别名
     */
    @ApiModelProperty(value = "字段别名")
    private String fieldAlias;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 字段类型
     */
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    /**
     * 字符串值
     */
    @ApiModelProperty(value = "字符串值")
    private String fieldStringValue;

    /**
     * 数字值
     */
    @ApiModelProperty(value = "数字值")
    private Long fieldNumberValue;

    /**
     * 描述性值
     */
    @ApiModelProperty(value = "描述性值")
    private String fieldTextValue;

    /**
     * 值hash
     */
    @ApiModelProperty(value = "值hash")
    private Long fieldHashValue;

    /**
     * 从表数据
     */
    @ApiModelProperty(value = "从表数据")
    private List<GridDataInfoModel> gridDataInfoModels;
}