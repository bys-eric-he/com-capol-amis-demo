package com.capol.amis.entity.bo;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:48
 * desc: 数据集关联关系
 */
@Data
public class DatasetUnionBO {
    /** 左表信息 */
    private DatasetTableBasicBO leftTable;
    /** 右表关联关系 */
    private Set<DatasetRightUnionBO> rightUnions;
    /** 查询字段(表id， 查询字段) */
    private Map<Long, Set<String>> datasetQueryFields;
}