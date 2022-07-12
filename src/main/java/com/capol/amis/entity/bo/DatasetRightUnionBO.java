package com.capol.amis.entity.bo;

import lombok.Data;

import java.util.Set;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:38
 * desc: 数据右表信息
 */
@Data
public class DatasetRightUnionBO {
    /** 右表信息 */
    private DatasetTableBasicBO rightTable;
    /** 关联字段 */
    private Set<DatasetUnionFieldBO> unionFields;
    /** 右表查询字段 */
    private Set<String> targetFields;
}
