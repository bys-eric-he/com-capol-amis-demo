package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.TemplateGridDataDO;
import com.capol.amis.mapper.TemplateGridDataMapper;
import com.capol.amis.service.ITemplateGridDataService;
import org.springframework.stereotype.Service;

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
}
