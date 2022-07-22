package com.capol.amis.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.capol.amis.entity.BusinessSubjectDO;
import com.capol.amis.entity.CfgFormDictDO;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.entity.TemplateGridConfDO;
import com.capol.amis.entity.base.BaseInfo;
import com.capol.amis.enums.*;
import com.capol.amis.mapper.TemplateFormConfMapper;
import com.capol.amis.mapper.TemplateGridConfMapper;
import com.capol.amis.model.FormDictModel;
import com.capol.amis.model.param.BusinessSubjectFormModel;
import com.capol.amis.model.param.FormFieldConfigModel;
import com.capol.amis.model.param.GridFieldConfigModel;
import com.capol.amis.service.*;
import com.capol.amis.utils.AmisUtil;
import com.capol.amis.utils.BaseInfoContextHolder;
import com.capol.amis.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmisFormConfigSeviceImpl /*extends ServiceTransactionDefinition*/ implements IAmisFormConfigSevice {
    /**
     * 雪花算法工具类
     */
    @Autowired
    private SnowflakeUtil snowflakeUtil;

    /**
     * 业务主题基本信息
     */
    @Autowired
    private IBusinessSubjectService iBusinessSubjectService;

    /**
     * 业务主题表单配置表
     */
    @Autowired
    private ITemplateFormConfService iTemplateFormConfService;

    /**
     * 业务主题列表配置表
     */
    @Autowired
    private ITemplateGridConfService iTemplateGridConfService;

    /**
     * 表单配置字典字段数据信表
     */
    @Autowired
    private ICfgFormDictService iCfgFormDictService;

    @Autowired
    private TemplateFormConfMapper templateFormConfMapper;

    @Autowired
    private TemplateGridConfMapper templateGridConfMapper;


    /**
     * 保存表单字段配置信息
     *
     * @param subjectFormModel
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public String saveFormFieldConfig(BusinessSubjectFormModel subjectFormModel) {
        boolean isExist = false;
        JSONObject configJson = JSONObject.parseObject(subjectFormModel.getConfigJson());
        QueryWrapper<BusinessSubjectDO> queryFormWrapper = new QueryWrapper<>();
        queryFormWrapper
                .eq("status", 1)
                .eq("subject_id", subjectFormModel.getSubjectId());
        BusinessSubjectDO existBusinessSubjectDO = iBusinessSubjectService.getOne(queryFormWrapper);
        if (existBusinessSubjectDO != null) {
            isExist = true;
        }
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
            //super.start();
            if (isExist) {
                QueryWrapper<BusinessSubjectDO> updateFormWrapper = new QueryWrapper<>();
                updateFormWrapper
                        .eq("status", 1)
                        .eq("subject_id", subjectFormModel.getSubjectId());
                //更新业务主题基本信息
                iBusinessSubjectService.update(businessSubjectDO, updateFormWrapper);
                log.info("更新业务主题基本信息完成!");
            } else {
                // 保存业务主题基本信息
                iBusinessSubjectService.save(businessSubjectDO);
                log.info("保存业务主题基本信息完成!");
            }

            saveHandle(enterpriseId, projectId, subjectFormModel);
            //super.commit();
        } catch (Exception exception) {
            log.error("保存表单字段配置信息异常, 异常原因：" + exception.getMessage());
            //super.rollback();
            return "****保存表单字段配置信息失败！****";
        }

        return "保存表单字段配置信息成功！";
    }

    /**
     * 获取配置表中，表ID与表类型关系
     */
    @Override
    public Map<Long, TableRelationTypeEnum> getTableRelationType() {
        Map<Long, TableRelationTypeEnum> tableRelationTypeMap = new HashMap<>();
        templateFormConfMapper.selectDistinctTableIds().forEach(tableId -> tableRelationTypeMap.put(tableId, TableRelationTypeEnum.MAIN_TYPE));
        templateGridConfMapper.selectDistinctTableIds().forEach(tableId -> tableRelationTypeMap.put(tableId, TableRelationTypeEnum.SUB_TYPE));
        return tableRelationTypeMap;
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
        //字典字段配置
        List<FormDictModel> formDictModels = new ArrayList<>();

        // 主表(表单)字段
        List<JSONObject> formFields = new ArrayList<>();

        // 从表(列表)字段(从表会有多个表的情况，因此需要使用Map)
        Map<String, List<JSONObject>> gridFields = new HashMap<>();

        // 表单JSON对象
        JSONObject formTable = AmisUtil.getFormObject(configJson);

        // 解析表单JSON
        parseFormBody(formFields, gridFields, formTable);

        // 主表ID
        Long formTableId = snowflakeUtil.nextId();

        // 根据业务主题ID查询该业务主题主表字段
        List<TemplateFormConfDO> formConfDOS = iTemplateFormConfService.getFieldsBySubjectId(subjectFormModel.getSubjectId());

        //根据业务主题ID查询该业务主题从表字段
        List<TemplateGridConfDO> gridConfDOS = iTemplateGridConfService.getFieldsBySubjectId(subjectFormModel.getSubjectId());

        // 主表新增字段
        List<FormFieldConfigModel> addFormFields = new ArrayList<>();
        // 主表修改字段
        List<FormFieldConfigModel> updateFormFields = new ArrayList<>();
        // 主表删除字段
        List<Long> deleteFormFields = new ArrayList<>();

        // 从表新增字段
        List<GridFieldConfigModel> addGridFields = new ArrayList<>();
        // 从表修改字段
        List<GridFieldConfigModel> updateGridFields = new ArrayList<>();
        // 从表删除字段
        List<Long> deleteGridFields = new ArrayList<>();

        // 如果存在该业务主题的表单信息，说明是修改表单操作。
        if (CollectionUtils.isNotEmpty(formConfDOS)) {
            // 解析主表增加、修改字段
            addOrUpdateFormFieldsHandle(subjectFormModel.getSubjectId(), formTableId, formFields, formConfDOS, addFormFields, updateFormFields, deleteFormFields, formDictModels);
            log.info("--->主表新增字段列表：{}", JSONObject.toJSONString(addFormFields));
            log.info("--->主表修改字段列表：{}", JSONObject.toJSONString(updateFormFields));
            //解析主表删除字段
            deleteFormFieldsHandle(formFields, formConfDOS, deleteFormFields);
            log.info("--->主表删除字段列表：{}", JSONObject.toJSONString(deleteFormFields));

            // 处理主表新增、删除、修改字段
            updateFromConfigHandle(enterpriseId, projectId, 100, addFormFields, updateFormFields, deleteFormFields);

            //如果该业务主题存在从表信息
            if (CollectionUtils.isNotEmpty(gridConfDOS)) {
                // 解析从表增加、修改字段
                addOrUpdateGridFieldsHandle(subjectFormModel.getSubjectId(), formTableId, gridFields, gridConfDOS, addGridFields, updateGridFields, deleteGridFields);
                log.info("--->从表新增字段列表：{}", JSONObject.toJSONString(addGridFields));
                log.info("--->从表修改字段列表：{}", JSONObject.toJSONString(updateGridFields));
                //解析从表删除字段
                deleteGridFieldsHandle(gridFields, gridConfDOS, deleteGridFields);
                log.info("--->从表删除字段列表：{}", JSONObject.toJSONString(deleteGridFields));
            }

            // 处理从表新增、删除、修改字段
            updateGridConfigHandle(enterpriseId, projectId, 100, addGridFields, updateGridFields, deleteGridFields);
            log.info("表单 {} 配置信息，更新完成!", subjectFormModel.getSubjectId());
        } else {
            // 处理主表数据
            List<FormFieldConfigModel> formFieldConfigModels = formTableHandle(formTableId, subjectFormModel, formFields, formDictModels);
            log.info("-->主表字段信息：" + JSONObject.toJSONString(formFieldConfigModels));
            log.info("-->主表字典字段信息：" + JSONObject.toJSONString(formDictModels));

            // 处理从表数据
            List<GridFieldConfigModel> gridFieldConfigModels = gridTableHandle(formTableId, subjectFormModel, gridFields);
            log.info("-->从表字段信息：" + JSONObject.toJSONString(gridFieldConfigModels));

            //字典字段配置
            List<CfgFormDictDO> cfgFormDictDOS = new ArrayList<>();
            int orderNumber = 1;
            for (FormDictModel formDictModel : formDictModels) {
                CfgFormDictDO cfgFormDictDO = new CfgFormDictDO();
                cfgFormDictDO.setTableName(formDictModel.getTableName());
                cfgFormDictDO.setFieldName(formDictModel.getFieldName());
                cfgFormDictDO.setDictLabel(formDictModel.getDictLabel());
                cfgFormDictDO.setDictValue(formDictModel.getDictValue());
                cfgFormDictDO.setOrderNo(orderNumber);
                cfgFormDictDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                cfgFormDictDOS.add(cfgFormDictDO);
                orderNumber++;
            }

            //保存表单字典配置字段信息
            if (CollectionUtils.isNotEmpty(cfgFormDictDOS)) {
                iCfgFormDictService.saveBatch(cfgFormDictDOS);
                log.info("保存表单字典配置字段信息完成!");
            }

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
                templateFormConfDO.setFieldLength(model.getFieldLength());
                templateFormConfDO.setComponentType(model.getComponentType());
                templateFormConfDO.setFieldDataType(model.getFieldDataType());
                templateFormConfDO.setFieldShowType(model.getFieldShowType());
                templateFormConfDO.setFieldSourceType(model.getFieldSourceType());
                templateFormConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                templateFormConfDOS.add(templateFormConfDO);
                orderNo++;
            }

            //保存主表配置信息
            if (CollectionUtils.isNotEmpty(templateFormConfDOS)) {
                iTemplateFormConfService.saveBatch(templateFormConfDOS);
                log.info("保存主表配置信息完成!");
            }

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
                templateGridConfDO.setFieldLength(model.getFieldLength());
                templateGridConfDO.setComponentType(model.getComponentType());
                templateGridConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                templateGridConfDOS.add(templateGridConfDO);
            }

            //保存从表配置信息
            if (CollectionUtils.isNotEmpty(templateGridConfDOS)) {
                iTemplateGridConfService.saveBatch(templateGridConfDOS);
                log.info("保存从表配置信息完成!");
            }
            log.info("新增表单配置信息，保存完成!!!");
        }
    }

    /**
     * 更新主表配置信息
     *
     * @param enterpriseId
     * @param projectId
     * @param orderNo
     * @param addFormFields
     * @param updateFormFields
     * @param deleteFormFields
     */
    private void updateFromConfigHandle(Long enterpriseId, Long projectId, int orderNo, List<FormFieldConfigModel> addFormFields, List<FormFieldConfigModel> updateFormFields,
                                        List<Long> deleteFormFields) {
        //1. 处理新增的字段
        List<TemplateFormConfDO> addTemplateFormConfDOS = new ArrayList<>();
        for (FormFieldConfigModel model : addFormFields) {
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
            templateFormConfDO.setFieldLength(model.getFieldLength());
            templateFormConfDO.setComponentType(model.getComponentType());
            templateFormConfDO.setFieldDataType(model.getFieldDataType());
            templateFormConfDO.setFieldShowType(model.getFieldShowType());
            templateFormConfDO.setFieldSourceType(model.getFieldSourceType());
            templateFormConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            addTemplateFormConfDOS.add(templateFormConfDO);
            orderNo++;
        }

        iTemplateFormConfService.saveBatch(addTemplateFormConfDOS);
        log.info("--->主表已新增字段：{}", JSONObject.toJSONString(addTemplateFormConfDOS));

        //2. 处理更新的字段
        List<TemplateFormConfDO> updateTemplateFormConfDOS = new ArrayList<>();
        for (FormFieldConfigModel model : updateFormFields) {
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
            templateFormConfDO.setFieldLength(model.getFieldLength());
            templateFormConfDO.setComponentType(model.getComponentType());
            templateFormConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            templateFormConfDO.setId(model.getId());
            updateTemplateFormConfDOS.add(templateFormConfDO);
            /*
            QueryWrapper<TemplateFormConfDO> updateFormWrapper = new QueryWrapper<>();
            updateFormWrapper
                    .eq("status", 1)
                    .eq("id", model.getId());
            iTemplateFormConfService.update(templateFormConfDO, updateFormWrapper);
            */
        }

        iTemplateFormConfService.updateBatchById(updateTemplateFormConfDOS);
        log.info("--->主表已更新字段：{}", JSONObject.toJSONString(updateTemplateFormConfDOS));

        //3. 处理删除的字段
        iTemplateFormConfService.removeByIds(deleteFormFields);
        log.info("--->主表已删除字段：{}", JSONObject.toJSONString(deleteFormFields));
    }


    /**
     * 更新从表配置信息
     *
     * @param enterpriseId
     * @param projectId
     * @param orderNo
     * @param addGridFields
     * @param updateGridFields
     * @param deleteGridFields
     */
    private void updateGridConfigHandle(Long enterpriseId, Long projectId, int orderNo, List<GridFieldConfigModel> addGridFields,
                                        List<GridFieldConfigModel> updateGridFields, List<Long> deleteGridFields) {
        //1. 处理新增的字段
        List<TemplateGridConfDO> addTemplateGridConfDOS = new ArrayList<>();
        for (GridFieldConfigModel model : addGridFields) {
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
            templateGridConfDO.setFieldLength(model.getFieldLength());
            templateGridConfDO.setComponentType(model.getComponentType());
            templateGridConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            addTemplateGridConfDOS.add(templateGridConfDO);
            orderNo++;
        }

        iTemplateGridConfService.saveBatch(addTemplateGridConfDOS);
        log.info("--->从表已新增字段：{}", JSONObject.toJSONString(addTemplateGridConfDOS));

        //2. 处理更新的字段
        List<TemplateGridConfDO> updateTemplateGridConfDOS = new ArrayList<>();
        for (GridFieldConfigModel model : updateGridFields) {
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
            templateGridConfDO.setFieldLength(model.getFieldLength());
            templateGridConfDO.setComponentType(model.getComponentType());
            templateGridConfDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            templateGridConfDO.setId(model.getId());
            updateTemplateGridConfDOS.add(templateGridConfDO);
            /*
            QueryWrapper<TemplateFormConfDO> updateFormWrapper = new QueryWrapper<>();
            updateFormWrapper
                    .eq("status", 1)
                    .eq("id", model.getId());
            iTemplateFormConfService.update(templateFormConfDO, updateFormWrapper);
            */
        }

        iTemplateGridConfService.updateBatchById(updateTemplateGridConfDOS);
        log.info("--->从表已更新字段：{}", JSONObject.toJSONString(updateTemplateGridConfDOS));

        //3. 处理删除的字段
        iTemplateGridConfService.removeByIds(deleteGridFields);
        log.info("--->从表已删除字段：{}", JSONObject.toJSONString(deleteGridFields));
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
     * @param formDictModels
     */
    private List<FormFieldConfigModel> formTableHandle(Long formTableId, BusinessSubjectFormModel subjectFormModel, List<JSONObject> formFields, List<FormDictModel> formDictModels) {
        //主表字段
        List<FormFieldConfigModel> mainFields = new ArrayList<>();
        //主表表名
        String formTableName = "cfg_table_" + subjectFormModel.getSubjectId();
        //构建系统字段
        buildFormSystemFields(formTableId, formTableName, mainFields, subjectFormModel);
        if (CollectionUtils.isNotEmpty(formFields)) {
            formFields.forEach(jsonObject -> {
                //根据组件类型构建字段Model
                buildFormFields(subjectFormModel.getSubjectId(), formTableId, formTableName, mainFields, formDictModels, jsonObject);
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
    private List<GridFieldConfigModel> gridTableHandle(Long formTableId, BusinessSubjectFormModel subjectFormModel, Map<String, List<JSONObject>> gridFields) {
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
                    buildGridFields(subjectFormModel.getSubjectId(), formTableId, gridTableId, gridTableName, subFields, jsonObject);
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
     * 主表添加或修改字段解析
     *
     * @param subjectId
     * @param formTableId
     * @param formFields
     * @param formFieldsDB
     * @param addFields
     * @param updateFields
     * @param deleteFields
     * @param formDictModels
     */
    private void addOrUpdateFormFieldsHandle(Long subjectId, Long formTableId, List<JSONObject> formFields, List<TemplateFormConfDO> formFieldsDB,
                                             List<FormFieldConfigModel> addFields, List<FormFieldConfigModel> updateFields,
                                             List<Long> deleteFields, List<FormDictModel> formDictModels) throws Exception {
        if (CollectionUtils.isEmpty(formFields)) {
            throw new Exception("表单组件不能为空，请重新检查再提交!");
        }
        //主表表名
        String formTableName = "cfg_table_" + subjectId;
        //遍历表单解析字段，解析出新增或修改的字段
        for (JSONObject jsonObject : formFields) {
            String type = jsonObject.getString(AmisUtil.TYPE);
            String key = jsonObject.getString(AmisUtil.NAME);
            //新增字段
            if (!formFieldsDB.stream().map(TemplateFormConfDO::getFieldKey).collect(Collectors.toList())
                    .contains(key)) {
                //新增字段
                log.info("-----主表有新增字段-----");
                buildFormFields(subjectId, formTableId, formTableName, addFields, formDictModels, jsonObject);
                log.info("字段信息：{}", key);
            } else {
                //修改字段
                Optional<TemplateFormConfDO> fieldsDBOptional = formFieldsDB.stream().filter(o -> o.getFieldKey().equals(key)).findFirst();
                if (fieldsDBOptional.isPresent()) {
                    TemplateFormConfDO templateFormConfDO = fieldsDBOptional.get();
                    //检测控件类型是否有改变
                    if (!type.equals(templateFormConfDO.getComponentType())) {
                        log.info("-----主表有修改字段，控件类型有改变-----");
                        log.info("原值：{}", templateFormConfDO.getComponentType());
                        //新增字段
                        buildFormFields(subjectId, formTableId, formTableName, addFields, formDictModels, jsonObject);
                        //逻辑删除原字段
                        List<Long> delFieldIds = formFieldsDB.stream().filter(item -> key.equals(item.getFieldKey()))
                                .map(BaseInfo::getId).collect(Collectors.toList());
                        deleteFields.addAll(delFieldIds);
                        log.info("-----主表删除原有字段：{}-----", JSONObject.toJSONString(delFieldIds));
                        continue;
                    }

                    String label = jsonObject.getString(AmisUtil.LABEL);
                    Integer maxLength = AmisUtil.getMaxLength(jsonObject);

                    FormFieldConfigModel updateFieldConfigModel = new FormFieldConfigModel();

                    BeanCopier formBeanCopier = BeanCopier.create(TemplateFormConfDO.class, FormFieldConfigModel.class, false);
                    formBeanCopier.copy(templateFormConfDO, updateFieldConfigModel, null);

                    updateFieldConfigModel.setId(null);

                    // 改了Label名称
                    if (!StringUtils.equals(label, templateFormConfDO.getFieldAlias())) {
                        updateFieldConfigModel.setId(templateFormConfDO.getId());
                        updateFieldConfigModel.setFieldAlias(label);
                        log.info("-----主表有修改字段，控件改了Label名称-----");
                        log.info("原值：{}", templateFormConfDO.getFieldAlias());
                        log.info("新值：{}", updateFieldConfigModel.getFieldAlias());
                    }
                    //改变了长度
                    if (maxLength != null && templateFormConfDO.getFieldLength() != null && maxLength > templateFormConfDO.getFieldLength()) {
                        updateFieldConfigModel.setId(templateFormConfDO.getId());
                        updateFieldConfigModel.setFieldLength(maxLength);
                        log.info("-----主表有修改字段，控件改变了长度-----");
                        log.info("原值：{}", templateFormConfDO.getFieldLength());
                        log.info("新值：{}", updateFieldConfigModel.getFieldLength());
                    }

                    //有主键说明需要更新
                    if (updateFieldConfigModel.getId() != null) {
                        updateFields.add(updateFieldConfigModel);
                    }
                }
            }
        }
    }

    /**
     * 从表添加或修改字段解析
     *
     * @param subjectId
     * @param formTableId
     * @param gridFields
     * @param gridFieldsDB
     * @param addFields
     * @param updateFields
     * @param deleteFields
     */
    private void addOrUpdateGridFieldsHandle(Long subjectId, Long formTableId, Map<String, List<JSONObject>> gridFields, List<TemplateGridConfDO> gridFieldsDB,
                                             List<GridFieldConfigModel> addFields, List<GridFieldConfigModel> updateFields,
                                             List<Long> deleteFields) throws Exception {
        if (gridFields == null || gridFields.size() == 0) {
            throw new Exception("从表表单组件不能为空，请重新检查再提交!");
        }

        for (Map.Entry<String, List<JSONObject>> entry : gridFields.entrySet()) {
            //遍历表单解析字段，解析出新增或修改的字段
            for (JSONObject jsonObject : entry.getValue()) {
                Long gridTableId = gridFieldsDB.stream().filter(gridField -> gridField.getGridTableName().equals(entry.getKey())).map(TemplateGridConfDO::getGridTableId).collect(Collectors.toList()).get(0);
                String gridTableName = entry.getKey();
                String type = jsonObject.getString(AmisUtil.TYPE);
                String key = jsonObject.getString(AmisUtil.NAME);
                //新增字段
                if (!gridFieldsDB.stream().map(TemplateGridConfDO::getFieldKey).collect(Collectors.toList())
                        .contains(key)) {
                    //新增字段
                    log.info("-----从表有新增字段,表名:{}-----", gridTableName);
                    buildGridFields(subjectId, formTableId, gridTableId, gridTableName, addFields, jsonObject);
                    log.info("字段信息：{}", key);
                } else {
                    //修改字段
                    Optional<TemplateGridConfDO> fieldsDBOptional = gridFieldsDB.stream().filter(o -> o.getFieldKey().equals(key)).findFirst();
                    if (fieldsDBOptional.isPresent()) {
                        TemplateGridConfDO templateGridConfDO = fieldsDBOptional.get();
                        //检测控件类型是否有改变
                        if (!type.equals(templateGridConfDO.getComponentType())) {
                            log.info("-----从表有修改字段，控件类型有改变-----");
                            log.info("原值：{}", templateGridConfDO.getComponentType());
                            //新增字段
                            buildGridFields(subjectId, formTableId, gridTableId, gridTableName, addFields, jsonObject);
                            //逻辑删除原字段
                            List<Long> delFieldIds = gridFieldsDB.stream().filter(item -> key.equals(item.getFieldKey()))
                                    .map(BaseInfo::getId).collect(Collectors.toList());
                            deleteFields.addAll(delFieldIds);
                            log.info("-----删除原有字段：{}-----", JSONObject.toJSONString(delFieldIds));
                            continue;
                        }

                        String label = jsonObject.getString(AmisUtil.LABEL);
                        Integer maxLength = AmisUtil.getMaxLength(jsonObject);

                        GridFieldConfigModel updateFieldConfigModel = new GridFieldConfigModel();

                        BeanCopier formBeanCopier = BeanCopier.create(TemplateGridConfDO.class, GridFieldConfigModel.class, false);
                        formBeanCopier.copy(templateGridConfDO, updateFieldConfigModel, null);

                        updateFieldConfigModel.setId(null);

                        // 改了Label名称
                        if (!StringUtils.equals(label, templateGridConfDO.getFieldAlias())) {
                            updateFieldConfigModel.setId(templateGridConfDO.getId());
                            updateFieldConfigModel.setFieldAlias(label);
                            log.info("-----从表有修改字段，控件改了Label名称-----");
                            log.info("原值：{}", templateGridConfDO.getFieldAlias());
                            log.info("新值：{}", updateFieldConfigModel.getFieldAlias());
                        }
                        //改变了长度
                        if (maxLength != null && templateGridConfDO.getFieldLength() != null && maxLength > templateGridConfDO.getFieldLength()) {
                            updateFieldConfigModel.setId(templateGridConfDO.getId());
                            updateFieldConfigModel.setFieldLength(maxLength);
                            log.info("-----从表有修改字段，控件改变了长度-----");
                            log.info("原值：{}", templateGridConfDO.getFieldLength());
                            log.info("新值：{}", updateFieldConfigModel.getFieldLength());
                        }

                        //有主键说明需要更新
                        if (updateFieldConfigModel.getId() != null) {
                            updateFields.add(updateFieldConfigModel);
                        }
                    }
                }
            }
        }
    }

    /**
     * 主表删除字段解析
     *
     * @param formFields
     * @param formFieldsDB
     * @param deleteFields
     */
    private void deleteFormFieldsHandle(List<JSONObject> formFields, List<TemplateFormConfDO> formFieldsDB, List<Long> deleteFields) {
        //当前表单提交的字段
        List<String> currentFieldsKey = formFields.stream().map(jsonObject -> (String) jsonObject.get(AmisUtil.NAME)).collect(Collectors.toList());
        //系统字段
        List<String> systemFields = SystemFieldEnum.getSystemFieldEnum().stream().map(SystemFieldEnum::getFieldName).collect(Collectors.toList());
        //和数据库中的字段作对比
        formFieldsDB.forEach(formField -> {
            if (!currentFieldsKey.contains(formField.getFieldKey()) && !systemFields.contains(formField.getFieldKey())) {
                log.info("-----主表有删除字段-----");
                log.info("字段值：{}->{}", formField.getFieldAlias(), formField.getFieldKey());
                //字段已经删除
                deleteFields.add(formField.getId());
            }
        });
    }

    /**
     * 从表删除字段解析
     *
     * @param gridFields
     * @param gridFieldsDB
     * @param deleteFields
     */
    private void deleteGridFieldsHandle(Map<String, List<JSONObject>> gridFields, List<TemplateGridConfDO> gridFieldsDB, List<Long> deleteFields) {
        for (Map.Entry<String, List<JSONObject>> entry : gridFields.entrySet()) {
            String gridTableName = entry.getKey();
            //系统字段
            List<String> systemFields = SystemFieldEnum.getSystemFieldEnum().stream().map(SystemFieldEnum::getFieldName).collect(Collectors.toList());
            //当前表单提交的字段
            List<String> currentFieldsKey = entry.getValue().stream().map(jsonObject -> (String) jsonObject.get(AmisUtil.NAME)).collect(Collectors.toList());
            //和数据库中的字段作对比
            gridFieldsDB.stream().filter(o -> o.getGridTableName().equals(gridTableName)).forEach(gridField -> {
                if (!currentFieldsKey.contains(gridField.getFieldKey()) && !systemFields.contains(gridField.getFieldKey())) {
                    log.info("-----从表有删除字段-----");
                    log.info("字段值：{}->{}", gridField.getFieldAlias(), gridField.getFieldKey());
                    //字段已经删除
                    deleteFields.add(gridField.getId());
                }
            });
        }
    }

    /**
     * 构建主表系统字段
     *
     * @param formTableId
     * @param formTableName
     * @param mainFields
     * @param subjectFormModel
     */
    private void buildFormSystemFields(Long formTableId, String formTableName, List<FormFieldConfigModel> mainFields, BusinessSubjectFormModel subjectFormModel) {

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
    private void buildGridSystemFields(Long formTableId, Long gridTableId, String gridTableName, List<GridFieldConfigModel> subFields, BusinessSubjectFormModel subjectFormModel) {

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
     * @param formDictModels
     * @param jsonObject
     */
    private void buildFormFields(Long subjectId, Long formTableId, String formTableName,
                                 List<FormFieldConfigModel> mainFields, List<FormDictModel> formDictModels, JSONObject jsonObject) {
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
                buildFormSelectFieldModel(subjectId, formTableId, formTableName, number, fieldEnum, mainFields, formDictModels, jsonObject);
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
    private void buildGridFields(Long subjectId, Long formTableId, Long gridTableId, String gridTableName,
                                 List<GridFieldConfigModel> gridFields, JSONObject jsonObject) {
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
    private void buildFormTextFieldModel(Long subjectId, Long formTableId, String formTableName, Long number,
                                         ComponentFieldEnum fieldEnum, List<FormFieldConfigModel> mainFields,
                                         JSONObject jsonObject) {
        //字段名称
        String fieldName = "column_" + number;
        //字段数据类型(1、单一值 2、原始值 3、转换值)
        Integer fieldDataType = FieldDataTypeEnum.ORIGINAL.getValue();
        // 字段显示类型(1、显示 2、隐藏)
        Integer fieldShowType = FieldShowTypeEnum.HIDE.getValue();
        // 字段来源类型(1、系统 2、自定义)
        Integer fieldSourceType = FieldSourceTypeEnum.SELF_DEFINED.getValue();
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        FormFieldConfigModel fieldConfigModel = buildFormFieldConfigModel(subjectId, formTableId, formTableName,
                jsonObject, fieldName, fieldEnum, fieldDataType, fieldShowType, fieldSourceType, fieldLength);

        mainFields.add(fieldConfigModel);
    }

    /**
     * 构建下拉类字段
     *
     * @param subjectId
     * @param formTableId
     * @param formTableName
     * @param number
     * @param fieldEnum
     * @param mainFields
     * @param dictModels
     * @param jsonObject
     */
    private void buildFormSelectFieldModel(Long subjectId, Long formTableId, String formTableName, Long number,
                                           ComponentFieldEnum fieldEnum, List<FormFieldConfigModel> mainFields,
                                           List<FormDictModel> dictModels, JSONObject jsonObject) {
        //字段名称
        String fieldName = "column_" + number;
        //字段数据类型(1、单一值 2、原始值 3、转换值)
        Integer fieldDataType = FieldDataTypeEnum.ORIGINAL.getValue();
        // 字段显示类型(1、显示 2、隐藏)
        Integer fieldShowType = FieldShowTypeEnum.HIDE.getValue();
        // 字段来源类型(1、系统 2、自定义)
        Integer fieldSourceType = FieldSourceTypeEnum.SELF_DEFINED.getValue();
        //是否有设置度度，有的话取设置的长度
        Integer maxLength = AmisUtil.getMaxLength(jsonObject);
        Integer fieldLength = maxLength == null ? fieldEnum.getFieldLength() : maxLength;

        //构建Model
        FormFieldConfigModel fieldConfigModel = buildFormFieldConfigModel(subjectId, formTableId, formTableName,
                jsonObject, fieldName, fieldEnum, fieldDataType, fieldShowType, fieldSourceType, fieldLength);

        mainFields.add(fieldConfigModel);

        //构建show Model
        FormFieldConfigModel showFieldConfigModel = new FormFieldConfigModel();
        BeanCopier formBeanCopier = BeanCopier.create(FormFieldConfigModel.class, FormFieldConfigModel.class, false);
        formBeanCopier.copy(fieldConfigModel, showFieldConfigModel, null);

        showFieldConfigModel.setFieldName("column_show_" + number);
        showFieldConfigModel.setFieldDataType(FieldDataTypeEnum.TRANSFORM.getValue());
        showFieldConfigModel.setFieldShowType(FieldShowTypeEnum.SHOW.getValue());

        mainFields.add(showFieldConfigModel);

        //构建表单常量
        JSONArray selectOptions = AmisUtil.getSelectOptions(jsonObject);
        if (CollectionUtils.isNotEmpty(selectOptions)) {
            for (Object selectOption : selectOptions) {
                JSONObject object = (JSONObject) selectOption;
                FormDictModel formDictModel = new FormDictModel();
                formDictModel.setTableName(formTableName);
                formDictModel.setFieldName("column_" + number);
                formDictModel.setDictLabel(object.getString(AmisUtil.LABEL));
                formDictModel.setDictValue(object.getString(AmisUtil.VALUE));
                dictModels.add(formDictModel);
            }
        }
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
    private void buildGridTextFieldModel(Long subjectId, Long number, ComponentFieldEnum fieldEnum, Long formTableId,
                                         Long gridTableId, String gridTableName, List<GridFieldConfigModel> gridFields,
                                         JSONObject jsonObject) {
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
     * @param fieldDataType
     * @param fieldShowType
     * @param fieldSourceType
     * @param fieldLength
     * @return
     */
    private FormFieldConfigModel buildFormFieldConfigModel(Long subjectId, Long formTableId, String formTableName,
                                                           JSONObject jsonObject, String fieldName, ComponentFieldEnum fieldEnum,
                                                           Integer fieldDataType, Integer fieldShowType,
                                                           Integer fieldSourceType, Integer fieldLength) {
        FormFieldConfigModel fieldConfigModel = new FormFieldConfigModel();
        fieldConfigModel.setSubjectId(subjectId);
        fieldConfigModel.setTableId(formTableId);
        fieldConfigModel.setTableName(formTableName);
        fieldConfigModel.setFieldKey(jsonObject.getString(AmisUtil.NAME));
        if (fieldEnum.getValue().equals(AmisUtil.PICKER)) {
            String nameStr = jsonObject.getString(AmisUtil.NAME);
            if (nameStr.contains("pickerDiy")) {
                fieldConfigModel.setFieldAlias("自定义关联组件");
            } else if (nameStr.contains("pickerProject")) {
                fieldConfigModel.setFieldAlias("项目台账组件");
            } else if (nameStr.contains("pickerStaff")) {
                fieldConfigModel.setFieldAlias("组织架构人员关联");
            }
        } else {
            fieldConfigModel.setFieldAlias(jsonObject.getString(AmisUtil.LABEL));
        }
        fieldConfigModel.setFieldType(fieldEnum.getFieldType());
        fieldConfigModel.setFieldName(fieldName);
        fieldConfigModel.setFieldLength(fieldLength);
        fieldConfigModel.setFieldDataType(fieldDataType);
        fieldConfigModel.setFieldShowType(fieldShowType);
        fieldConfigModel.setFieldSourceType(fieldSourceType);
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
    private GridFieldConfigModel buildGridFieldConfigModel(Long subjectId, Long formTableId, Long gridTableId,
                                                           String gridTableName, JSONObject jsonObject, String fieldName,
                                                           ComponentFieldEnum fieldEnum, Integer fieldLength) {
        GridFieldConfigModel fieldConfigModel = new GridFieldConfigModel();
        fieldConfigModel.setSubjectId(subjectId);
        fieldConfigModel.setFormTableId(formTableId);
        fieldConfigModel.setGridTableId(gridTableId);
        fieldConfigModel.setGridTableName(gridTableName);
        fieldConfigModel.setFieldKey(jsonObject.getString(AmisUtil.NAME));
        fieldConfigModel.setFieldAlias(jsonObject.getString(AmisUtil.LABEL));
        fieldConfigModel.setFieldType(fieldEnum.getFieldType());
        fieldConfigModel.setFieldName(fieldName);
        fieldConfigModel.setFieldLength(fieldLength);
        fieldConfigModel.setComponentType(fieldEnum.getValue());

        return fieldConfigModel;
    }
}
