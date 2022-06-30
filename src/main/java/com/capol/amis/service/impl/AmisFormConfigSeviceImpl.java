package com.capol.amis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.BusinessSubjectDO;
import com.capol.amis.enums.ComponentFieldEnum;
import com.capol.amis.enums.SystemFieldEnum;
import com.capol.amis.model.BusinessSubjectFormModel;
import com.capol.amis.model.FormFieldConfigModel;
import com.capol.amis.service.*;
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
public class AmisFormConfigSeviceImpl implements IAmisFormConfigSevice {

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
     * 业务主题表单数据表 服务类
     */
    @Autowired
    private ITemplateFormDataService iTemplateFormDataService;

    /**
     * 业务主题列表配置表 服务类
     */
    @Autowired
    private ITemplateGridConfService iTemplateGridConfService;

    /**
     * 业务主题列表数据表 服务类
     */
    @Autowired
    private ITemplateGridDataService iTemplateGridDataService;

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
        businessSubjectDO.setConfigJson(subjectFormModel.getConfigJson());
        businessSubjectDO.setEnterpriseId(snowflakeUtil.nextId());
        businessSubjectDO.setProjectId(snowflakeUtil.nextId());
        businessSubjectDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
        businessSubjectDO.setStatus(1);

        try {
            // 保存业务主题基本信息
            iBusinessSubjectService.save(businessSubjectDO);
            saveHandle(subjectFormModel);
        } catch (Exception exception) {
            log.error("保存表单字段配置信息异常, 异常原因：" + exception.getMessage());
        }

        return "保存表单字段配置信息成功！";
    }

    /**
     * 处理表单配置信息
     *
     * @param subjectFormModel
     * @return
     */
    private String saveHandle(BusinessSubjectFormModel subjectFormModel) throws Exception {

        String configJson = subjectFormModel.getConfigJson();
        // 主表(表单)字段
        List<JSONObject> formFields = new ArrayList<>();
        // 从表(列表)字段
        Map<String, List<JSONObject>> gridFields = new HashMap<>();

        // 从表映射Map, key是表名标识, value是从表字段对象
        Map<String, JSONObject> gridTableMap = new HashMap<>();

        // 主表映射Map
        JSONObject formTable = AmisUtil.getFormObject(configJson);

        // 解析表单JSON
        parseFormBody(formFields, gridFields, gridTableMap, formTable);

        // 处理主表数据
        List<FormFieldConfigModel> formFieldConfigModels = formTableHandle(subjectFormModel, formFields);
        log.info("-->主表字段信息：" + JSONObject.toJSONString(formFieldConfigModels));

        // 处理从表数据


        return null;
    }

    /**
     * 解析表单JSON和校验
     *
     * @param formFields
     * @param gridFields
     * @param gridTableMap
     * @param formTable
     */
    private void parseFormBody(List<JSONObject> formFields, Map<String, List<JSONObject>> gridFields, Map<String, JSONObject> gridTableMap, JSONObject formTable) throws Exception {
        if (formTable == null) {
            throw new Exception("表单组件不能为空，请重新检查再提交!");
        }

        //开始解析表单JSON
        try {
            AmisUtil.parseFormBody(formFields, gridFields, gridTableMap, formTable);
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
        buildSystemFields(mainFields, subjectFormModel);

        if (CollectionUtils.isNotEmpty(formFields)) {
            formFields.forEach(jsonObject -> {
                //根据组件类型构建字段Model
                buildFormFields(subjectFormModel.getSubjectId(), mainFields, jsonObject);
            });
        }

        return mainFields;
    }

    /**
     * 构建系统字段
     *
     * @param mainFields
     */
    private void buildSystemFields(List<FormFieldConfigModel> mainFields, BusinessSubjectFormModel subjectFormModel) {

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
                buildTextFieldModel(subjectId, number, fieldEnum, mainFields, jsonObject);
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
     * 构建文本类字段
     *
     * @param subjectId
     * @param number
     * @param fieldEnum
     * @param mainFields
     * @param jsonObject
     */
    private void buildTextFieldModel(Long subjectId, Long number, ComponentFieldEnum fieldEnum, List<FormFieldConfigModel> mainFields, JSONObject jsonObject) {
        String fieldName = "column_" + number;
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        FormFieldConfigModel fieldConfigModel = buildFormFieldConfigModel(subjectId, jsonObject, fieldName, fieldEnum, fieldLength);

        mainFields.add(fieldConfigModel);
    }

    /**
     * 构建Model
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
}
