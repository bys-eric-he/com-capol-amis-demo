package com.capol.amis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capol.amis.entity.TemplateGridDataDO;
import com.capol.amis.entity.bo.TemplateDataBO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    Map<Long, Map<String, Optional<TemplateDataBO>>> queryClassifiedGridDataByTableId(Long tableId);
}
