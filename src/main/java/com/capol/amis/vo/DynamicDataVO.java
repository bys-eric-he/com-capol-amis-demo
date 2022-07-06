package com.capol.amis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 业务主题一览表数据VO
 */
@Data
@ApiModel(value = "业务主题表单数据主表一览表")
public class DynamicDataVO {
    /**
     * 数据总条数
     */
    @ApiModelProperty(value = "数据总条数")
    private Long totalRows;

    /**
     * 数据行
     */
    @ApiModelProperty(value = "数据行")
    private List<Map<String, Object>> rows;

    /**
     * 数据列
     */
    @ApiModelProperty(value = "数据列")
    private List<DynamicFieldsVO> header;

    public DynamicDataVO(){
        this.setRows(new ArrayList<>());
        this.setHeader(new ArrayList<>());
        this.setTotalRows(0L);
    }
}
