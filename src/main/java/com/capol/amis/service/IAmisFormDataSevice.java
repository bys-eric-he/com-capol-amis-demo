package com.capol.amis.service;

import com.capol.amis.model.param.BusinessSubjectDataModel;
import com.capol.amis.model.result.FormDataInfoModel;
import com.capol.amis.vo.DynamicDataVO;

import java.util.List;
import java.util.Map;

public interface IAmisFormDataSevice {

    /**
     * 插入表单数据
     *
     * @param businessSubjectDataModel
     * @return
     */
    String insertData(BusinessSubjectDataModel businessSubjectDataModel);

    /**
     * 更新表单数据
     *
     * @param businessSubjectDataModel
     * @return
     */
    String updateData(BusinessSubjectDataModel businessSubjectDataModel);

    /**
     * 根据数据行号删除数据
     *
     * @param subjectId
     * @param rowId
     * @return
     */
    String deleteData(Long subjectId, Long rowId);

    /**
     * 查询表单数据(主表+从表)
     *
     * @param subjectId
     * @return
     */
    List<FormDataInfoModel> queryFormDataList(Long subjectId);

    /**
     * 查询表单数据(行转列-仅主表数据)
     *
     * @param subjectId
     * @return
     */
    DynamicDataVO queryFormDataMaps(Long subjectId);

    /**
     * 获取指定表单数据明细（包括从表数据）
     *
     * @param subjectId
     * @param rowId
     * @return
     */
    Map<String, Object> getDetail(Long subjectId, Long rowId);

}
