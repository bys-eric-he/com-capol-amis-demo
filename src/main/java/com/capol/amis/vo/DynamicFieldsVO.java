package com.capol.amis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务主题表单一览表从表详细信息
 */
@Data
@ApiModel(value = "业务主题表单一览表从表详细信息")
@NoArgsConstructor
public class DynamicFieldsVO {
    /**
     * 字段ID
     */
    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    /**
     * 表名
     */
    @ApiModelProperty(value = "表名")
    private String tableName;

    /**
     * 列名
     */
    @ApiModelProperty(value = "列名")
    private String fieldName;

    /**
     * 字段注释
     */
    @ApiModelProperty(value = "字段注释")
    private String fieldAlias;

    /**
     * 映射的表单组件名
     */
    @ApiModelProperty(value = "映射的表单组件名")
    private String fieldKey;

    /**
     * 字段类型
     */
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    /**
     * 字段顺序
     */
    @ApiModelProperty(value = "字段顺序")
    private Integer fieldOrder;

}
