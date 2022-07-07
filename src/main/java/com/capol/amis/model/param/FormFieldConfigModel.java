package com.capol.amis.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 业务主题表单配置信息
 */
@ApiModel(value = "业务主题表单字段信息")
@Data
public class FormFieldConfigModel {

    @ApiModelProperty(value = "业务主题ID")
    @NotNull(message = "业务主题ID不能为空!")
    private Long subjectId;

    @ApiModelProperty(value = "字段名注释")
    @NotNull(message = "字段名注释不能为空!")
    private String fieldAlias;

    @ApiModelProperty(value = "字段名")
    @NotNull(message = "字段名不能为空!")
    private String fieldName;

    @ApiModelProperty(value = "字段名标识")
    @NotNull(message = "字段名标识不能为空!")
    private String fieldKey;

    @ApiModelProperty(value = "字段长度")
    @NotNull(message = "字段长度不能为空!")
    private Integer fieldLength;

    @ApiModelProperty(value = "字段类型")
    @NotNull(message = "字段类型不能为空!")
    private String fieldType;

    @ApiModelProperty(value = "字段是否允许为空")
    @NotNull(message = "字段是否允许为空不能为空!")
    private Integer fieldNull;

    @ApiModelProperty(value = "组件类型")
    @NotNull(message = "组件类型不能为空!")
    private String componentType;

}
