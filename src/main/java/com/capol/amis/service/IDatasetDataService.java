package com.capol.amis.service;

import com.capol.amis.entity.bo.DatasetUnionBO;

import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 15:27
 * desc: 数据集数据服务
 */
public interface IDatasetDataService {
    /**
     * 获取联表数据集信息
     * @param datasetUnion 连表相关信息
     * @return 获取的信息, list中每一项表示一行, map中的key是字段id, value是字段值
     */
    List<Map<Long, Object>> getUnionJoinDatas(DatasetUnionBO datasetUnion);

    List<DatasetUnionBO> getUnionFields();
}
