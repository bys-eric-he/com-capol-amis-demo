package com.capol.amis.service;

import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.model.param.BusinessSubjectFormModel;

import java.util.Map;

public interface IAmisFormConfigSevice {

    /**
     * 保存表单字段配置信息
     *
     * @param subjectFormModel
     * @return
     */
    String saveFormFieldConfig(BusinessSubjectFormModel subjectFormModel) throws Exception;

    /**
     * 获取配置表中，表ID与表类型关系
     *
     * @return
     */
    Map<Long, TableRelationTypeEnum> getTableRelationType();
}
