package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.service.ITemplateFormDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务主题表单数据表 服务实现类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Service
public class TemplateFormDataServiceImpl
        extends ServiceImpl<TemplateFormDataMapper, TemplateFormDataDO>
        implements ITemplateFormDataService {

    @Autowired
    private TemplateFormDataMapper templateFormDataMapper;

    /**
     * 根据业务主题Id查询主表数据
     *
     * @param subjectId
     * @return
     */
    @Override
    public List<TemplateFormDataDO> queryFromDataBySubjectId(Long subjectId) {
        return templateFormDataMapper.queryFromDataBySubjectId(subjectId);
    }


    @Override
    public Map<Long, Map<String, Optional<TemplateFormDataDO>>> queryClassifiedFromDataByTableId(Long tableId) {
        return new LambdaQueryChainWrapper<>(templateFormDataMapper)
                // TODO zyx 改TableId
                .eq(TemplateFormDataDO::getSubjectId, tableId).list().stream()
                .collect(Collectors.groupingBy(TemplateFormDataDO::getRowId,
                        Collectors.groupingBy(TemplateFormDataDO::getFieldName, Collectors.reducing((ldata, rdata) -> ldata))));
    }
}
