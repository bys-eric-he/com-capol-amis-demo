package com.capol.amis.entity.bo;

import com.capol.amis.enums.TableRelationTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:11
 * desc: 数据集表基本信息
 */
@Data
@Accessors(chain = true)
public class DatasetTableBasicBO {
    /**
     * 表ID:
     * TODO zyx 主题表:subject_id 列表配置表: table_name
     */
    private Long tableId;
    /**
     * 表格类型 {@link com.capol.amis.enums.TableRelationTypeEnum}
     */
    private TableRelationTypeEnum tableType;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表别名
     */
    private String tableAliasName;
}
