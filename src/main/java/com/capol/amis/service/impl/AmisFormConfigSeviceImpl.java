package com.capol.amis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.BusinessSubjectDO;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.entity.TemplateGridConfDO;
import com.capol.amis.enums.ComponentFieldEnum;
import com.capol.amis.enums.SystemFieldEnum;
import com.capol.amis.model.BusinessSubjectFormModel;
import com.capol.amis.model.FormFieldConfigModel;
import com.capol.amis.model.GridFieldConfigModel;
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
import java.util.List;

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

        // 从表(列表)字段
        List<JSONObject> gridFields = new ArrayList<>();

        // 表单JSON对象
        JSONObject formTable = AmisUtil.getFormObject(configJson);

        // 解析表单JSON
        parseFormBody(formFields, gridFields, formTable);

        // 处理主表数据
        List<FormFieldConfigModel> formFieldConfigModels = formTableHandle(subjectFormModel, formFields);
        log.info("-->主表字段信息：" + JSONObject.toJSONString(formFieldConfigModels));

        // 处理从表数据
        List<GridFieldConfigModel> gridFieldConfigModels = gridTableHandle(subjectFormModel, gridFields);
        log.info("-->从表字段信息：" + JSONObject.toJSONString(gridFieldConfigModels));

        List<TemplateFormConfDO> templateFormConfDOS = new ArrayList<>();
        int orderNo = 1;
        for (FormFieldConfigModel model : formFieldConfigModels) {
            TemplateFormConfDO templateFormConfDO = new TemplateFormConfDO();
            templateFormConfDO.setEnterpriseId(enterpriseId);
            templateFormConfDO.setProjectId(projectId);
            templateFormConfDO.setSubjectId(model.getSubjectId());
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
        int orderNumber = 1;
        for (GridFieldConfigModel model : gridFieldConfigModels) {
            TemplateGridConfDO templateGridConfDO = new TemplateGridConfDO();
            templateGridConfDO.setEnterpriseId(enterpriseId);
            templateGridConfDO.setProjectId(projectId);
            templateGridConfDO.setSubjectId(model.getSubjectId());
            templateGridConfDO.setFieldAlias(model.getFieldAlias());
            templateGridConfDO.setFieldKey(model.getFieldKey());
            templateGridConfDO.setFieldName(model.getFieldName());
            templateGridConfDO.setFieldOrder(orderNumber);
            templateGridConfDO.setFieldType(model.getFieldType());
            templateGridConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());

            templateGridConfDOS.add(templateGridConfDO);
            orderNumber++;
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
    private void parseFormBody(List<JSONObject> formFields, List<JSONObject> gridFields, JSONObject formTable) throws Exception {
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
     * @param subjectFormModel
     * @param formFields
     */
    private List<FormFieldConfigModel> formTableHandle(BusinessSubjectFormModel subjectFormModel, List<JSONObject> formFields) {
        //主表字段
        List<FormFieldConfigModel> mainFields = new ArrayList<>();
        //构建系统字段
        buildFormSystemFields(mainFields, subjectFormModel);

        if (CollectionUtils.isNotEmpty(formFields)) {
            formFields.forEach(jsonObject -> {
                //根据组件类型构建字段Model
                buildFormFields(subjectFormModel.getSubjectId(), mainFields, jsonObject);
            });
        }

        return mainFields;
    }

    /**
     * 从表处理
     *
     * @param subjectFormModel
     * @param gridFields
     * @return
     */
    private List<GridFieldConfigModel> gridTableHandle(BusinessSubjectFormModel subjectFormModel, List<JSONObject> gridFields) {
        //主表字段
        List<GridFieldConfigModel> subFields = new ArrayList<>();
        //构建系统字段
        buildGridSystemFields(subFields, subjectFormModel);

        if (CollectionUtils.isNotEmpty(gridFields)) {
            gridFields.forEach(jsonObject -> {
                //根据组件类型构建字段Model
                buildGridFields(subjectFormModel.getSubjectId(), subFields, jsonObject);
            });
        }

        return subFields;
    }

    /**
     * 构建主表系统字段
     *
     * @param mainFields
     */
    private void buildFormSystemFields(List<FormFieldConfigModel> mainFields, BusinessSubjectFormModel subjectFormModel) {

        List<SystemFieldEnum> systemFieldEnums = SystemFieldEnum.getMainTableEnum();

        systemFieldEnums.forEach(systemFieldEnum -> {
            FormFieldConfigModel fieldConfigModel = new FormFieldConfigModel();
            fieldConfigModel.setSubjectId(subjectFormModel.getSubjectId());
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
     * @param subFields
     */
    private void buildGridSystemFields(List<GridFieldConfigModel> subFields, BusinessSubjectFormModel subjectFormModel) {

        List<SystemFieldEnum> systemFieldEnums = SystemFieldEnum.getMainTableEnum();

        systemFieldEnums.forEach(systemFieldEnum -> {
            GridFieldConfigModel fieldConfigModel = new GridFieldConfigModel();
            fieldConfigModel.setSubjectId(subjectFormModel.getSubjectId());
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
     * @param mainFields
     * @param jsonObject
     */
    private void buildFormFields(Long subjectId, List<FormFieldConfigModel> mainFields, JSONObject jsonObject) {
        Long number = snowflakeUtil.nextId();
        String type = jsonObject.getString(AmisUtil.TYPE);
        ComponentFieldEnum fieldEnum = ComponentFieldEnum.getEnumByType(type);
        switch (fieldEnum.getGroup()) {
            case 1: {
                //文本字段类
                buildFormTextFieldModel(subjectId, number, fieldEnum, mainFields, jsonObject);
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
     * @param gridFields
     * @param jsonObject
     */
    private void buildGridFields(Long subjectId, List<GridFieldConfigModel> gridFields, JSONObject jsonObject) {
        Long number = snowflakeUtil.nextId();
        String type = jsonObject.getString(AmisUtil.TYPE);
        ComponentFieldEnum fieldEnum = ComponentFieldEnum.getEnumByType(type);
        switch (fieldEnum.getGroup()) {
            case 1: {
                //文本字段类
                buildGridTextFieldModel(subjectId, number, fieldEnum, gridFields, jsonObject);
                break;
            }
        }
    }

    /**
     * 构建文本类字段
     *
     * @param subjectId
     * @param number
     * @param fieldEnum
     * @param mainFields
     * @param jsonObject
     */
    private void buildFormTextFieldModel(Long subjectId, Long number, ComponentFieldEnum fieldEnum, List<FormFieldConfigModel> mainFields, JSONObject jsonObject) {
        String fieldName = "column_" + number;
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        FormFieldConfigModel fieldConfigModel = buildFormFieldConfigModel(subjectId, jsonObject, fieldName, fieldEnum, fieldLength);

        mainFields.add(fieldConfigModel);
    }

    /**
     * 构建文本类字段
     *
     * @param subjectId
     * @param number
     * @param fieldEnum
     * @param gridFields
     * @param jsonObject
     */
    private void buildGridTextFieldModel(Long subjectId, Long number, ComponentFieldEnum fieldEnum, List<GridFieldConfigModel> gridFields, JSONObject jsonObject) {
        String fieldName = "column_" + number;
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        GridFieldConfigModel fieldConfigModel = buildGridFieldConfigModel(subjectId, jsonObject, fieldName, fieldEnum, fieldLength);

        gridFields.add(fieldConfigModel);
    }

    /**
     * 构建主表Model
     *
     * @param subjectId
     * @param jsonObject
     * @param fieldName
     * @param fieldEnum
     * @param fieldLength
     * @return
     */
    private FormFieldConfigModel buildFormFieldConfigModel(Long subjectId, JSONObject jsonObject, String fieldName, ComponentFieldEnum fieldEnum, Integer fieldLength) {
        FormFieldConfigModel fieldConfigModel = new FormFieldConfigModel();
        fieldConfigModel.setSubjectId(subjectId);
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
     * @param jsonObject
     * @param fieldName
     * @param fieldEnum
     * @param fieldLength
     * @return
     */
    private GridFieldConfigModel buildGridFieldConfigModel(Long subjectId, JSONObject jsonObject, String fieldName, ComponentFieldEnum fieldEnum, Integer fieldLength) {
        GridFieldConfigModel fieldConfigModel = new GridFieldConfigModel();
        fieldConfigModel.setSubjectId(subjectId);
        fieldConfigModel.setFieldKey(jsonObject.getString("name"));
        fieldConfigModel.setFieldAlias(jsonObject.getString("label"));
        fieldConfigModel.setFieldType(fieldEnum.getFieldType());
        fieldConfigModel.setFieldName(fieldName);
        fieldConfigModel.setFieldLength(fieldLength);
        fieldConfigModel.setComponentType(fieldEnum.getValue());

        return fieldConfigModel;
    }
}
