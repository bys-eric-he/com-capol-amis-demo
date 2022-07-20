package com.capol.amis.service;

import com.capol.amis.entity.DatasetUnionDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyaxi
 * @since 2022-07-18 19:38
 * desc: 针对表【t_dataset_union_324225698253233659(数据集关联表)】的数据库操作Service
 */
public interface IDatasetUnionService extends IService<DatasetUnionDO> {

    // 获取所有右表所有关联字段
    Map<Long, List<DatasetUnionDO>> getAllUnionMap();
}
