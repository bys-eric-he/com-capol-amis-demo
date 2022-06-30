package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.mapper.TemplateFormConfMapper;
import com.capol.amis.service.ITemplateFormConfService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务主题表单配置表 服务实现类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Service
public class TemplateFormConfServiceImpl
        extends ServiceImpl<TemplateFormConfMapper, TemplateFormConfDO>
        implements ITemplateFormConfService {
}
