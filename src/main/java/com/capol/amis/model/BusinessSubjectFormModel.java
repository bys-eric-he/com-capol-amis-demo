package com.capol.amis.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 业务主题表单信息
 */
@ApiModel(value = "业务主题表单配置信息")
@Data
public class BusinessSubjectFormModel {
    @ApiModelProperty(value = "业务主题ID")
    @NotNull(message = "业务主题ID不能为空!")
    private Long subjectId;

    @ApiModelProperty(value = "提交类型")
    @NotNull(message = "提交类型不能为空!")
    private Integer submitType;

    @ApiModelProperty(value = "表单配置JSON")
    @NotNull(message = "表单配置JSON不能为空!")
    private String configJson;
}
