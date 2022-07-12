package com.capol.amis.entity.bo;


import lombok.Data;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:11
 * desc: 数据集字段关联关系
 */
@Data
public class DatasetUnionFieldBO {
    /** 左表字段 */
    private String leftFieldName;
    /** 左表字段别名 */
    private String leftFieldNameAlias;
    /** 右表字段 */
    private String rightFieldName;
    /** 右表字段别名 */
    private String rightFieldNameAlias;
}
