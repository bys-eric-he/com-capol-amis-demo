package com.capol.amis.service;

import com.capol.amis.model.BusinessSubjectDataModel;

public interface IAmisFormDataSevice {

    /**
     * 插入表单数据
     *
     * @param businessSubjectDataModel
     * @return
     */
    String insertData(BusinessSubjectDataModel businessSubjectDataModel);
}
