package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.service.ITemplateFormDataService;
import org.springframework.stereotype.Service;

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
}
