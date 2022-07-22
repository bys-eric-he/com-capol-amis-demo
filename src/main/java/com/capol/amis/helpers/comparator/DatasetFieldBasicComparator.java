package com.capol.amis.helpers.comparator;

import com.capol.amis.entity.bo.DatasetFieldBasicBO;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

import com.capol.amis.helpers.constants.DatasetSqlConstants;

/**
 * @author Yaxi.Zhang
 * @desc
 * @since 2022/7/21 16:45
 */
@Slf4j
public class DatasetFieldBasicComparator implements Comparator<DatasetFieldBasicBO> {
    @Override
    public int compare(DatasetFieldBasicBO o1, DatasetFieldBasicBO o2) {
        String[] fs1 = o1.getFieldNameAlias().split(DatasetSqlConstants.FIELD_NAME_ALIAS_PREFIX);
        String[] fs2 = o2.getFieldNameAlias().split(DatasetSqlConstants.FIELD_NAME_ALIAS_PREFIX);
        try {
            return Integer.parseInt(fs1[1]) - Integer.parseInt(fs2[1]);
        } catch (Exception e) {
            log.error("依据列表别名查找排序字段失败");
        }
        return 1;
    }
}
