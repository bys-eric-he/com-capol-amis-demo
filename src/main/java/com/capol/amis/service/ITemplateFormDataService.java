package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.TemplateFormDataDO;

import java.util.List;

/**
 * <p>
 * 业务主题表单数据表 服务类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
public interface ITemplateFormDataService extends IService<TemplateFormDataDO> {
    /**
     * 根据业务主题Id查询主表数据
     *
     * @param subjectId
     * @return
     */
    List<TemplateFormDataDO> queryFromDataBySubjectId(Long subjectId);
}
