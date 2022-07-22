package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.CfgFormDictDO;

import java.util.List;

/**
 * <p>
 * 表单字典表 服务类
 * </p>
 *
 * @author He.Yong
 * @since 2022-07-21
 */
public interface ICfgFormDictService extends IService<CfgFormDictDO> {

    /**
     * 根据表名获取该表配置的字典字段信息
     *
     * @param tableName
     * @return
     */
    List<CfgFormDictDO> getDictByTableName(String tableName);

    /**
     * 从字典表中获取展示字段值
     *
     * @param formDictDOS
     * @param fieldShowName
     * @param dictValue
     * @return
     */
    String getDictLabel(List<CfgFormDictDO> formDictDOS, String fieldShowName, String dictValue);
}
