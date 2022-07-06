package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.TemplateGridDataDO;

import java.util.List;

/**
 * <p>
 * 业务主题列表数据表 服务类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
public interface ITemplateGridDataService extends IService<TemplateGridDataDO> {
    /**
     * 根据业务主题ID获取业务主题列表数据
     *
     * @param subjectId
     * @return
     */
    List<TemplateGridDataDO> queryGridDataBySubjectId(Long subjectId);
}
