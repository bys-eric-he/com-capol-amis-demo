package com.capol.amis.entity.bo;

import lombok.Data;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:41
 * desc: 数据集字段基本信息
 */
@Data
public class DatasetFieldBasicBO {
    /** 表名 */
    private String tableName;
    /** 表别名 */
    private String tableNameAlias;
    /** 字段名称 */
    private String fieldName;
    /** 字段别名 */
    private String fieldNameAlias;
}
