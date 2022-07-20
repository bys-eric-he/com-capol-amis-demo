package com.capol.amis.service;

import com.capol.amis.entity.DatasetFieldDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangyaxi
 * @since 2022-07-18 19:43
 * desc: 针对表【t_dataset_field_324225698253233659(数据集字段表)】的数据库操作Service
 */
public interface IDatasetFieldService extends IService<DatasetFieldDO> {

    Map<Long, Map<Long, Set<Long>>> getQueryFields();
}
