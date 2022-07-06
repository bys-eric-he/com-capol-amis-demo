package com.capol.amis.service;

import com.capol.amis.model.param.BusinessSubjectFormModel;

public interface IAmisFormConfigSevice {

    /**
     * 保存表单字段配置信息
     *
     * @param subjectFormModel
     * @return
     */
    String saveFormFieldConfig(BusinessSubjectFormModel subjectFormModel);
}
