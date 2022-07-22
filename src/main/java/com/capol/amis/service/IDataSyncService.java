package com.capol.amis.service;

import com.capol.amis.entity.bo.DatasetUnionBO;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:27
 * desc: 数据同步
 */
public interface IDataSyncService {

    void registerJobHandler(String jobHandler);

    void syncDataToClickhouse(DatasetUnionBO datasetUnionBO);
}
