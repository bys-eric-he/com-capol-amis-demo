package com.capol.amis.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:38
 * desc: 数据右表信息
 */
@Data
@Accessors(chain = true)
public class DatasetRightUnionBO {
    /** 右表信息 */
    private DatasetTableBasicBO rightTable;
    /** 关联字段 */
    private Set<DatasetUnionFieldBO> unionFields;
}
