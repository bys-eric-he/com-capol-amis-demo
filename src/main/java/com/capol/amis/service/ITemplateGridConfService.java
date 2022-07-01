package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.TemplateGridConfDO;

import java.util.List;

/**
 * <p>
 * 业务主题列表配置表 服务类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
public interface ITemplateGridConfService extends IService<TemplateGridConfDO> {
    /**
     * 根据业务主题ID获取字段信息
     *
     * @param subjectId
     * @return
     */
    List<TemplateGridConfDO> getFieldsBySubjectId(Long subjectId);
}
