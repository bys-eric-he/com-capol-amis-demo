package com.capol.amis.service;

import com.capol.amis.model.BusinessSubjectFormModel;
import com.capol.amis.model.FormFieldConfigModel;

public interface IAmisFormConfigSevice {

    /**
     * 保存表单字段配置信息
     *
     * @param subjectFormModel
     * @return
     */
    String saveFormFieldConfig(BusinessSubjectFormModel subjectFormModel);
}
