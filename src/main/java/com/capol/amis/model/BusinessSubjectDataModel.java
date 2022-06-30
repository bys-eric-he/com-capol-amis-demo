package com.capol.amis.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 业务主题表单数据
 */
@ApiModel(value = "业务主题表单数据")
@Data
public class BusinessSubjectDataModel {
    @ApiModelProperty(value = "业务主题ID")
    @NotNull(message = "业务主题ID不能为空!")
    private Long subjectId;

    @ApiModelProperty(value = "表单数据JSON")
    @NotNull(message = "表单数据JSON不能为空!")
    private String dataJson;

    @ApiModelProperty(value = "表单数据状态")
    @NotNull(message = "数据状态不能为空!")
    private Integer status;
}
