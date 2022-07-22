package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.entity.CfgFormDictDO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 表单字典表 Mapper 接口
 * </p>
 *
 * @author He.Yong
 * @since 2022-07-21
 */
@Mapper
public interface CfgFormDictMapper extends BaseMapper<CfgFormDictDO> {
    /**
     * 根据表名获取该表配置的字典字段信息
     *
     * @param tableName
     * @return
     */
    List<CfgFormDictDO> getDictByTableName(@Param("tableName") String tableName);
}
