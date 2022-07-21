package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.DatasetFieldDO;
import com.capol.amis.service.IDatasetFieldService;
import com.capol.amis.mapper.DatasetFieldMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangyaxi
 * @since 2022-07-18 19:43
 * desc: 针对表【t_dataset_field(数据集字段表)】的数据库操作Service实现
 */
@Service
public class DatasetFieldServiceImpl extends ServiceImpl<DatasetFieldMapper, DatasetFieldDO>
        implements IDatasetFieldService {

    /**
     * 获取所有查询字段
     * map(数据集ID, map(表ID, 字段主键))
     */
    @Override
    public Map<Long, Map<Long, Set<Long>>> getQueryFields() {
        return baseMapper
                .selectList(new LambdaQueryWrapper<DatasetFieldDO>().eq(DatasetFieldDO::getStatus, 1))
                .stream().collect(Collectors.groupingBy(
                        DatasetFieldDO::getDatasetId,
                        Collectors.groupingBy(DatasetFieldDO::getTableId,
                                Collectors.mapping(DatasetFieldDO::getFieldId, Collectors.toSet())
                        )
                ));
    }

}
