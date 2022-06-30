package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.TemplateFormConfDO;

import java.util.List;

/**
 * <p>
 * 业务主题表单配置表 服务类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
public interface ITemplateFormConfService extends IService<TemplateFormConfDO> {

    /**
     * 根据业务主题ID获取字段信息
     *
     * @param subjectId
     * @return
     */
    List<TemplateFormConfDO> getFieldsBySubjectId(Long subjectId);
}
