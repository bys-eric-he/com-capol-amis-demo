package com.capol.amis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.BusinessSubjectDO;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.entity.TemplateGridConfDO;
import com.capol.amis.enums.ComponentFieldEnum;
import com.capol.amis.enums.SystemFieldEnum;
import com.capol.amis.model.param.BusinessSubjectFormModel;
import com.capol.amis.model.param.FormFieldConfigModel;
import com.capol.amis.model.param.GridFieldConfigModel;
import com.capol.amis.service.IAmisFormConfigSevice;
import com.capol.amis.service.IBusinessSubjectService;
import com.capol.amis.service.ITemplateFormConfService;
import com.capol.amis.service.ITemplateGridConfService;
import com.capol.amis.service.transaction.ServiceTransactionDefinition;
import com.capol.amis.utils.AmisUtil;
import com.capol.amis.utils.BaseInfoContextHolder;
import com.capol.amis.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AmisFormConfigSeviceImpl extends ServiceTransactionDefinition implements IAmisFormConfigSevice {
    /**
     * 雪花算法工具类
     */
    @Autowired
    private SnowflakeUtil snowflakeUtil;

    /**
     * 业务主题基本信息 服务类
     */
    @Autowired
    private IBusinessSubjectService iBusinessSubjectService;

    /**
     * 业务主题表单配置表 服务类
     */
    @Autowired
    private ITemplateFormConfService iTemplateFormConfService;

    /**
     * 业务主题列表配置表 服务类
     */
    @Autowired
    private ITemplateGridConfService iTemplateGridConfService;


    /**
     * 保存表单字段配置信息
     *
     * @param subjectFormModel
     * @return
     */
    @Override
    public String saveFormFieldConfig(BusinessSubjectFormModel subjectFormModel) {
        JSONObject configJson = JSONObject.parseObject(subjectFormModel.getConfigJson());
        BusinessSubjectDO businessSubjectDO = new BusinessSubjectDO();
        businessSubjectDO.setSubjectId(subjectFormModel.getSubjectId());
        if (configJson != null) {
            String subjectName = configJson.getString("title");
            businessSubjectDO.setSubjectName(subjectName);
        } else {
            businessSubjectDO.setSubjectName("AMIS-DEMO-业务主题");
        }

        Long enterpriseId = BaseInfoContextHolder.getEnterpriseAndProjectInfo().getEnterpriseId();
        Long projectId = BaseInfoContextHolder.getEnterpriseAndProjectInfo().getProjectId();

        businessSubjectDO.setConfigJson(subjectFormModel.getConfigJson());
        businessSubjectDO.setEnterpriseId(enterpriseId);
        businessSubjectDO.setProjectId(projectId);
        businessSubjectDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
        try {
            super.start();
            // 保存业务主题基本信息
            iBusinessSubjectService.save(businessSubjectDO);
            log.info("保存业务主题基本信息完成!");
            saveHandle(enterpriseId, projectId, subjectFormModel);
            super.commit();
        } catch (Exception exception) {
            log.error("保存表单字段配置信息异常, 异常原因：" + exception.getMessage());
            super.rollback();
            return "****保存表单字段配置信息失败！****";
        }

        return "保存表单字段配置信息成功！";
    }

    /**
     * 处理表单配置信息
     *
     * @param enterpriseId
     * @param projectId
     * @param subjectFormModel
     * @return
     */
    private void saveHandle(Long enterpriseId, Long projectId, BusinessSubjectFormModel subjectFormModel) throws Exception {
        String configJson = subjectFormModel.getConfigJson();
        // 主表(表单)字段
        List<JSONObject> formFields = new ArrayList<>();

        // 从表(列表)字段(从表会有多个表的情况，因此需要使用Map)
        Map<String, List<JSONObject>> gridFields = new HashMap<>();

        // 表单JSON对象
        JSONObject formTable = AmisUtil.getFormObject(configJson);

        // 解析表单JSON
        parseFormBody(formFields, gridFields, formTable);

        //主表ID
        Long formTableId = snowflakeUtil.nextId();

        // 处理主表数据
        List<FormFieldConfigModel> formFieldConfigModels = formTableHandle(formTableId, subjectFormModel, formFields);
        log.info("-->主表字段信息：" + JSONObject.toJSONString(formFieldConfigModels));

        // 处理从表数据
        List<GridFieldConfigModel> gridFieldConfigModels = gridTableHandle(formTableId, subjectFormModel, gridFields);
        log.info("-->从表字段信息：" + JSONObject.toJSONString(gridFieldConfigModels));

        List<TemplateFormConfDO> templateFormConfDOS = new ArrayList<>();
        int orderNo = 1;

        for (FormFieldConfigModel model : formFieldConfigModels) {
            TemplateFormConfDO templateFormConfDO = new TemplateFormConfDO();
            templateFormConfDO.setEnterpriseId(enterpriseId);
            templateFormConfDO.setProjectId(projectId);
            templateFormConfDO.setSubjectId(model.getSubjectId());
            templateFormConfDO.setTableId(model.getTableId());
            templateFormConfDO.setTableName(model.getTableName());
            templateFormConfDO.setFieldAlias(model.getFieldAlias());
            templateFormConfDO.setFieldKey(model.getFieldKey());
            templateFormConfDO.setFieldName(model.getFieldName());
            templateFormConfDO.setFieldOrder(orderNo);
            templateFormConfDO.setFieldType(model.getFieldType());
            templateFormConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            templateFormConfDOS.add(templateFormConfDO);
            orderNo++;
        }

        iTemplateFormConfService.saveBatch(templateFormConfDOS);

        List<TemplateGridConfDO> templateGridConfDOS = new ArrayList<>();
        for (GridFieldConfigModel model : gridFieldConfigModels) {
            TemplateGridConfDO templateGridConfDO = new TemplateGridConfDO();
            templateGridConfDO.setEnterpriseId(enterpriseId);
            templateGridConfDO.setProjectId(projectId);
            templateGridConfDO.setSubjectId(model.getSubjectId());
            templateGridConfDO.setFormTableId(model.getFormTableId());
            templateGridConfDO.setGridTableId(model.getGridTableId());
            templateGridConfDO.setGridTableName(model.getGridTableName());
            templateGridConfDO.setFieldAlias(model.getFieldAlias());
            templateGridConfDO.setFieldKey(model.getFieldKey());
            templateGridConfDO.setFieldName(model.getFieldName());
            templateGridConfDO.setFieldOrder(model.getFieldOrder());
            templateGridConfDO.setFieldType(model.getFieldType());
            templateGridConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            templateGridConfDOS.add(templateGridConfDO);
        }

        iTemplateGridConfService.saveBatch(templateGridConfDOS);
        log.info("保存表单配置信息完成!");
    }

    /**
     * 解析表单JSON和校验
     *
     * @param formFields
     * @param gridFields
     * @param formTable
     */
    private void parseFormBody(List<JSONObject> formFields, Map<String, List<JSONObject>> gridFields, JSONObject formTable) throws Exception {
        if (formTable == null) {
            throw new Exception("表单组件不能为空，请重新检查再提交!");
        }
        //开始解析表单JSON
        try {
            AmisUtil.parseFormBody(formFields, gridFields, formTable);
        } catch (Exception exception) {
            throw new Exception("解析表单异常，请重新检查再提交, 异常原因：" + exception.getMessage());
        }
    }

    /**
     * 主表处理
     *
     * @param formTableId
     * @param subjectFormModel
     * @param formFields
     */
    private List<FormFieldConfigModel> formTableHandle(Long formTableId, BusinessSubjectFormModel subjectFormModel, List<JSONObject> formFields) {
        //主表字段
        List<FormFieldConfigModel> mainFields = new ArrayList<>();
        //主表表名
        String formTableName = "cfg_table_" + subjectFormModel.getSubjectId();
        //构建系统字段
        buildFormSystemFields(formTableId, formTableName, mainFields, subjectFormModel);
        if (CollectionUtils.isNotEmpty(formFields)) {
            formFields.forEach(jsonObject -> {
                //根据组件类型构建字段Model
                buildFormFields(subjectFormModel.getSubjectId(), formTableId, formTableName, mainFields, jsonObject);
            });
        }

        return mainFields;
    }

    /**
     * 从表处理
     *
     * @param formTableId
     * @param subjectFormModel
     * @param gridFields
     * @return
     */
    private List<GridFieldConfigModel> gridTableHandle(Long formTableId, BusinessSubjectFormModel subjectFormModel,
                                                       Map<String, List<JSONObject>> gridFields) {
        //主表字段
        List<GridFieldConfigModel> subFields = new ArrayList<>();
        if (gridFields != null && gridFields.size() > 0) {
            for (Map.Entry<String, List<JSONObject>> entry : gridFields.entrySet()) {
                Long gridTableId = snowflakeUtil.nextId();
                String gridTableName = entry.getKey();
                List<JSONObject> value = entry.getValue();
                //构建系统字段
                buildGridSystemFields(formTableId, gridTableId, gridTableName, subFields, subjectFormModel);
                value.forEach(jsonObject -> {
                    //根据组件类型构建字段Model
                    buildGridFields(subjectFormModel.getSubjectId(), formTableId, gridTableId,
                            gridTableName, subFields, jsonObject);
                });
            }
        }
        int orderNo = 0;
        Long lastGridTableId = 0L;
        //设置顺序
        for (GridFieldConfigModel subField : subFields) {
            //如果是新的表，则从1重新开始设置序号
            if (lastGridTableId == 0L || subField.getGridTableId().equals(lastGridTableId)) {
                orderNo++;
            } else {
                orderNo = 1;
            }
            lastGridTableId = subField.getGridTableId();
            subField.setFieldOrder(orderNo);
        }
        return subFields;
    }

    /**
     * 构建主表系统字段
     *
     * @param formTableId
     * @param formTableName
     * @param mainFields
     * @param subjectFormModel
     */
    private void buildFormSystemFields(Long formTableId, String formTableName, List<FormFieldConfigModel> mainFields,
                                       BusinessSubjectFormModel subjectFormModel) {

        List<SystemFieldEnum> systemFieldEnums = SystemFieldEnum.getSystemFieldEnum();

        systemFieldEnums.forEach(systemFieldEnum -> {
            FormFieldConfigModel fieldConfigModel = new FormFieldConfigModel();
            fieldConfigModel.setSubjectId(subjectFormModel.getSubjectId());
            fieldConfigModel.setTableId(formTableId);
            fieldConfigModel.setTableName(formTableName);
            fieldConfigModel.setFieldKey(systemFieldEnum.getFieldName());
            fieldConfigModel.setFieldAlias(systemFieldEnum.getFieldAlias());
            fieldConfigModel.setFieldName(systemFieldEnum.getFieldName());
            fieldConfigModel.setFieldType(systemFieldEnum.getFieldType());
            fieldConfigModel.setFieldLength(systemFieldEnum.getFieldLength());
            fieldConfigModel.setFieldNull(systemFieldEnum.getFieldNull());
            fieldConfigModel.setComponentType(null);
            mainFields.add(fieldConfigModel);
        });
    }

    /**
     * 构建从表系统字段
     *
     * @param formTableId
     * @param gridTableId
     * @param gridTableName
     * @param subFields
     * @param subjectFormModel
     */
    private void buildGridSystemFields(Long formTableId, Long gridTableId, String gridTableName,
                                       List<GridFieldConfigModel> subFields, BusinessSubjectFormModel subjectFormModel) {

        List<SystemFieldEnum> systemFieldEnums = SystemFieldEnum.getSystemFieldEnum();

        systemFieldEnums.forEach(systemFieldEnum -> {
            GridFieldConfigModel fieldConfigModel = new GridFieldConfigModel();
            fieldConfigModel.setSubjectId(subjectFormModel.getSubjectId());
            fieldConfigModel.setFormTableId(formTableId);
            fieldConfigModel.setGridTableId(gridTableId);
            fieldConfigModel.setGridTableName(gridTableName);
            fieldConfigModel.setFieldKey(systemFieldEnum.getFieldName());
            fieldConfigModel.setFieldAlias(systemFieldEnum.getFieldAlias());
            fieldConfigModel.setFieldName(systemFieldEnum.getFieldName());
            fieldConfigModel.setFieldType(systemFieldEnum.getFieldType());
            fieldConfigModel.setFieldLength(systemFieldEnum.getFieldLength());
            fieldConfigModel.setFieldNull(systemFieldEnum.getFieldNull());
            fieldConfigModel.setComponentType(null);
            subFields.add(fieldConfigModel);
        });
    }

    /**
     * 构建表单业务字段
     *
     * @param subjectId
     * @param formTableId
     * @param formTableName
     * @param mainFields
     * @param jsonObject
     */
    private void buildFormFields(Long subjectId, Long formTableId, String formTableName, List<FormFieldConfigModel> mainFields, JSONObject jsonObject) {
        Long number = snowflakeUtil.nextId();
        String type = jsonObject.getString(AmisUtil.TYPE);
        ComponentFieldEnum fieldEnum = ComponentFieldEnum.getEnumByType(type);
        switch (fieldEnum.getGroup()) {
            case 1: {
                //文本字段类
                buildFormTextFieldModel(subjectId, formTableId, formTableName, number, fieldEnum, mainFields, jsonObject);
                break;
            }
            case 2: {
                // 选项下拉类
                log.info("选项下拉类,暂不支持!!!");
                break;
            }
            case 3: {
                //双字段类
                log.info("双字段类,暂不支持!!!");
                break;
            }
            default: {

            }
        }
    }

    /**
     * 构建列表业务字段
     * 列表只有文本字段
     *
     * @param subjectId
     * @param formTableId
     * @param gridTableId
     * @param gridTableName
     * @param gridFields
     * @param jsonObject
     */
    private void buildGridFields(Long subjectId, Long formTableId, Long gridTableId, String gridTableName, List<GridFieldConfigModel> gridFields, JSONObject jsonObject) {
        Long number = snowflakeUtil.nextId();
        String type = jsonObject.getString(AmisUtil.TYPE);
        ComponentFieldEnum fieldEnum = ComponentFieldEnum.getEnumByType(type);
        switch (fieldEnum.getGroup()) {
            case 1: {
                //文本字段类
                buildGridTextFieldModel(subjectId, number, fieldEnum, formTableId, gridTableId, gridTableName, gridFields, jsonObject);
                break;
            }
        }
    }

    /**
     * 构建文本类字段
     *
     * @param subjectId
     * @param formTableId
     * @param formTableName
     * @param number
     * @param fieldEnum
     * @param mainFields
     * @param jsonObject
     */
    private void buildFormTextFieldModel(Long subjectId, Long formTableId, String formTableName, Long number, ComponentFieldEnum fieldEnum, List<FormFieldConfigModel> mainFields, JSONObject jsonObject) {
        String fieldName = "column_" + number;
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        FormFieldConfigModel fieldConfigModel = buildFormFieldConfigModel(subjectId, formTableId, formTableName, jsonObject, fieldName, fieldEnum, fieldLength);

        mainFields.add(fieldConfigModel);
    }

    /**
     * 构建文本类字段
     *
     * @param subjectId
     * @param number
     * @param fieldEnum
     * @param formTableId
     * @param gridTableId
     * @param gridTableName
     * @param gridFields
     * @param jsonObject
     */
    private void buildGridTextFieldModel(Long subjectId, Long number, ComponentFieldEnum fieldEnum, Long formTableId, Long gridTableId, String gridTableName, List<GridFieldConfigModel> gridFields, JSONObject jsonObject) {
        String fieldName = "column_" + number;
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        GridFieldConfigModel fieldConfigModel = buildGridFieldConfigModel(subjectId, formTableId, gridTableId, gridTableName, jsonObject, fieldName, fieldEnum, fieldLength);

        gridFields.add(fieldConfigModel);
    }

    /**
     * 构建主表Model
     *
     * @param subjectId
     * @param formTableId
     * @param formTableName
     * @param jsonObject
     * @param fieldName
     * @param fieldEnum
     * @param fieldLength
     * @return
     */
    private FormFieldConfigModel buildFormFieldConfigModel(Long subjectId, Long formTableId, String formTableName, JSONObject jsonObject, String fieldName, ComponentFieldEnum fieldEnum, Integer fieldLength) {
        FormFieldConfigModel fieldConfigModel = new FormFieldConfigModel();
        fieldConfigModel.setSubjectId(subjectId);
        fieldConfigModel.setTableId(formTableId);
        fieldConfigModel.setTableName(formTableName);
        fieldConfigModel.setFieldKey(jsonObject.getString("name"));
        if (fieldEnum.getValue().equals("picker")) {
            String nameStr = jsonObject.getString("name");
            if (nameStr.contains("pickerDiy")) {
                fieldConfigModel.setFieldAlias("自定义关联组件");
            } else if (nameStr.contains("pickerProject")) {
                fieldConfigModel.setFieldAlias("项目台账组件");
            } else if (nameStr.contains("pickerStaff")) {
                fieldConfigModel.setFieldAlias("组织架构人员关联");
            }
        } else {
            fieldConfigModel.setFieldAlias(jsonObject.getString("label"));
        }
        fieldConfigModel.setFieldType(fieldEnum.getFieldType());
        fieldConfigModel.setFieldName(fieldName);
        fieldConfigModel.setFieldLength(fieldLength);
        fieldConfigModel.setComponentType(fieldEnum.getValue());

        return fieldConfigModel;
    }

    /**
     * 构建从表Model
     * 从表只有text组件
     *
     * @param subjectId
     * @param formTableId
     * @param gridTableId
     * @param gridTableName
     * @param jsonObject
     * @param fieldName
     * @param fieldEnum
     * @param fieldLength
     * @return
     */
    private GridFieldConfigModel buildGridFieldConfigModel(Long subjectId, Long formTableId, Long gridTableId, String gridTableName, JSONObject jsonObject, String fieldName, ComponentFieldEnum fieldEnum, Integer fieldLength) {
        GridFieldConfigModel fieldConfigModel = new GridFieldConfigModel();
        fieldConfigModel.setSubjectId(subjectId);
        fieldConfigModel.setFormTableId(formTableId);
        fieldConfigModel.setGridTableId(gridTableId);
        fieldConfigModel.setGridTableName(gridTableName);
        fieldConfigModel.setFieldKey(jsonObject.getString("name"));
        fieldConfigModel.setFieldAlias(jsonObject.getString("label"));
        fieldConfigModel.setFieldType(fieldEnum.getFieldType());
        fieldConfigModel.setFieldName(fieldName);
        fieldConfigModel.setFieldLength(fieldLength);
        fieldConfigModel.setComponentType(fieldEnum.getValue());

        return fieldConfigModel;
    }
}
