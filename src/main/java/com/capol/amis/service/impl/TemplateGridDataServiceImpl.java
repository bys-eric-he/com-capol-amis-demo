package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.TemplateGridDataDO;
import com.capol.amis.entity.bo.TemplateDataBO;
import com.capol.amis.mapper.TemplateGridDataMapper;
import com.capol.amis.service.ITemplateGridDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务主题列表数据表 服务实现类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Service
public class TemplateGridDataServiceImpl
        extends ServiceImpl<TemplateGridDataMapper, TemplateGridDataDO>
        implements ITemplateGridDataService {

    @Autowired
    private TemplateGridDataMapper templateGridDataMapper;

    /**
     * 根据业务主题ID获取业务主题列表数据
     *
     * @param subjectId
     * @return
     */
    @Override
    public List<TemplateGridDataDO> queryGridDataBySubjectId(Long subjectId) {
        return templateGridDataMapper.queryGridDataBySubjectId(subjectId);
    }

    @Override
    public Map<Long, Map<String, Optional<TemplateDataBO>>> queryClassifiedGridDataByTableId(Long tableId) {
        return new LambdaQueryChainWrapper<>(templateGridDataMapper)
                .eq(TemplateGridDataDO::getGridTableId, tableId).list().stream()
                .map(data -> {
                    TemplateDataBO templateDataBO = new TemplateDataBO();
                    BeanUtils.copyProperties(data, templateDataBO);
                    return templateDataBO;
                }).collect(Collectors.groupingBy(TemplateDataBO::getRowId,
                        Collectors.groupingBy(TemplateDataBO::getFieldName, Collectors.reducing((ldata, rdata) -> ldata))));
    }
}
