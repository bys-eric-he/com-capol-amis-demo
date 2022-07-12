package com.capol.amis.entity.bo;

import lombok.Data;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:11
 * desc: 数据集表基本信息
 */
@Data
public class DatasetTableBasicBO {
    /**
     * 表ID:
     * TODO zyx 主题表:subject_id 列表配置表: table_name
     */
    private Long tableId;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表别名
     */
    private String tableAliasName;
}
