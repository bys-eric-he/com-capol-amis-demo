package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.DatasetUnionDO;
import com.capol.amis.mapper.DatasetUnionMapper;
import com.capol.amis.service.IDatasetUnionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyaxi
 * @since 2022-07-18 19:38
 * desc: 针对表【t_dataset_union(数据集关联表)】的数据库操作Service实现
 */
@Service
@Slf4j
public class DatasetUnionServiceImpl extends ServiceImpl<DatasetUnionMapper, DatasetUnionDO>
        implements IDatasetUnionService {
    /**
     * 依据数据集id对关联信息进行分组
     */
    @Override
    public Map<Long, List<DatasetUnionDO>> getAllUnionMap() {
        // map(datasetId, datalist)
        LambdaQueryWrapper<DatasetUnionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasetUnionDO::getStatus, 1);
        // return list(lambdaQuery().eq(DatasetUnionDO::getStatus, 1))
        //         .stream()
        //         .collect(Collectors.groupingBy(DatasetUnionDO::getDatasetId));
        return list(queryWrapper)
                .stream()
                .collect(Collectors.groupingBy(DatasetUnionDO::getDatasetId));
    }

}
