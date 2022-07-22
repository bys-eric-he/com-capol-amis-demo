package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.BusinessSubjectDO;

/**
 * <p>
 * 业务主题基本信息 服务类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
public interface IBusinessSubjectService extends IService<BusinessSubjectDO> {

    /**
     * 根据业务主题ID获取表单配置JSON
     *
     * @param subjectId
     * @return
     */
    String getConfigJson(Long subjectId);
}
