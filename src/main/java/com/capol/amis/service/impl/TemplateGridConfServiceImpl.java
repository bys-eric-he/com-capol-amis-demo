package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.TemplateGridConfDO;
import com.capol.amis.mapper.TemplateGridConfMapper;
import com.capol.amis.service.ITemplateGridConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 业务主题列表配置表 服务实现类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Service
public class TemplateGridConfServiceImpl
        extends ServiceImpl<TemplateGridConfMapper, TemplateGridConfDO>
        implements ITemplateGridConfService {

    @Autowired
    private TemplateGridConfMapper templateGridConfMapper;

    /**
     * 根据业务主题ID获取字段信息
     *
     * @param subjectId
     * @return
     */
    @Override
    public List<TemplateGridConfDO> getFieldsBySubjectId(Long subjectId) {
        return templateGridConfMapper.getFieldsBySubjectId(subjectId);
    }
}
