package com.capol.amis.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HashUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.capol.amis.entity.*;
import com.capol.amis.enums.SystemFieldEnum;
import com.capol.amis.model.param.BusinessSubjectDataModel;
import com.capol.amis.model.result.FormDataInfoModel;
import com.capol.amis.model.result.GridDataInfoModel;
import com.capol.amis.service.*;
import com.capol.amis.utils.BaseInfoContextHolder;
import com.capol.amis.utils.RowConvertColUtil;
import com.capol.amis.utils.SnowflakeUtil;
import com.capol.amis.vo.DynamicDataVO;
import com.capol.amis.vo.DynamicFieldsVO;
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

import static com.capol.amis.enums.SystemFieldEnum.getSystemFieldEnum;

/**
 * 表单数据录入服务类
 */
@Slf4j
@Service
public class AmisFormDataSeviceImpl /*extends ServiceTransactionDefinition*/ implements IAmisFormDataSevice {
    /**
     * 雪花算法工具类
     */
    @Autowired
    private SnowflakeUtil snowflakeUtil;

    /**
     * 表单配置信息
     */
    @Autowired
    private ITemplateFormConfService iTemplateFormConfService;

    /**
     * 列表配置信息
     */
    @Autowired
    private ITemplateGridConfService iTemplateGridConfService;

    /**
     * 表单数据信息
     */
    @Autowired
    private ITemplateFormDataService iTemplateFormDataService;

    /**
     * 列表数据信息
     */
    @Autowired
    private ITemplateGridDataService iTemplateGridDataService;

    /**
     * 插入表单数据
     *
     * @param businessSubjectDataModel
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public String insertData(BusinessSubjectDataModel businessSubjectDataModel) {
        //super.start();
        try {
            checkValidate(businessSubjectDataModel);

            // 根据业务主题ID获取表单配置信息
            List<TemplateFormConfDO> templateFormConfDOS = iTemplateFormConfService.getFieldsBySubjectId(businessSubjectDataModel.getSubjectId());
            if (CollectionUtils.isEmpty(templateFormConfDOS)) {
                throw new Exception("表单字段配置表中暂无该业务主题的相关配置！");
            }

            // 根据业务主题ID获取列表配置信息
            List<TemplateGridConfDO> templateGridConfDOS = iTemplateGridConfService.getFieldsBySubjectId(businessSubjectDataModel.getSubjectId());
            if (CollectionUtils.isEmpty(templateGridConfDOS)) {
                log.warn("------业务主题：{} 从表没有配置信息!", businessSubjectDataModel.getSubjectId());
            }

            JSONObject jsonObject = JSON.parseObject(businessSubjectDataModel.getDataJson());

            if (null == jsonObject) {
                throw new Exception("JSON解析失败！");
            }

            //主表数据
            List<TemplateFormDataDO> templateFormDataDOS = new ArrayList<>();

            //从表数据
            List<TemplateGridDataDO> templateGridDataDOS = new ArrayList<>();

            //主表数据行ID
            Long rowId = snowflakeUtil.nextId();

            //遍历传入的JSON数据
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String fieldKey = entry.getKey();
                Object dataValue = entry.getValue();
                if (!(dataValue instanceof JSONArray)) {
                    //遍历主表业务主题字段
                    for (TemplateFormConfDO confDO : templateFormConfDOS) {
                        if (confDO.getFieldKey().equals(fieldKey)) {
                            //添加业务字段数据
                            TemplateFormDataDO dataDO = new TemplateFormDataDO();
                            String column = confDO.getFieldName();
                            String value = dataValue.toString();
                            dataDO.setRowId(rowId);
                            dataDO.setEnterpriseId(confDO.getEnterpriseId());
                            dataDO.setProjectId(confDO.getProjectId());
                            dataDO.setSubjectId(confDO.getSubjectId());
                            dataDO.setTemplateId(confDO.getId());
                            dataDO.setTableId(confDO.getTableId());
                            dataDO.setTableName(confDO.getTableName());
                            dataDO.setFieldAlias(confDO.getFieldAlias());
                            dataDO.setFieldKey(confDO.getFieldKey());
                            dataDO.setFieldName(column);
                            dataDO.setFieldType(confDO.getFieldType());
                            dataDO.setFieldTextValue(value);
                            dataDO.setFieldHashValue(HashUtil.mixHash(value));
                            dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                            templateFormDataDOS.add(dataDO);
                        }
                    }
                } else if (fieldKey.startsWith("table_") && dataValue instanceof JSONArray) {
                    log.info("-->解析列表(table)数据, 表名：{}", fieldKey);
                    String tableName = fieldKey;
                    //如果是表格则将值转换成数组
                    JSONArray gridValues = (JSONArray) dataValue;
                    //遍历从表业务主题字段
                    for (Object obj : gridValues) {
                        log.info("-->列表数据： {}", JSONObject.toJSONString(obj));
                        JSONObject gridObject = (JSONObject) obj;
                        //从表数据行ID
                        Long subRowId = snowflakeUtil.nextId();
                        //遍历传入的JSON数据
                        for (Map.Entry<String, Object> grid : gridObject.entrySet()) {
                            String gridKey = grid.getKey();
                            Object gridValue = grid.getValue();
                            //遍历业务主题列表字段
                            for (TemplateGridConfDO confDO : templateGridConfDOS) {
                                if (confDO.getFieldKey().equals(gridKey)) {
                                    TemplateGridDataDO dataDO = new TemplateGridDataDO();
                                    String column = confDO.getFieldName();
                                    String value = gridValue.toString();
                                    dataDO.setRowId(subRowId);
                                    dataDO.setFormRowId(rowId);
                                    dataDO.setEnterpriseId(confDO.getEnterpriseId());
                                    dataDO.setProjectId(confDO.getProjectId());
                                    dataDO.setSubjectId(confDO.getSubjectId());
                                    dataDO.setTemplateId(confDO.getId());
                                    dataDO.setFormTableId(confDO.getFormTableId());
                                    dataDO.setGridTableId(confDO.getGridTableId());
                                    dataDO.setGridTableName(tableName);
                                    dataDO.setFieldAlias(confDO.getFieldAlias());
                                    dataDO.setFieldKey(confDO.getFieldKey());
                                    dataDO.setFieldName(column);
                                    dataDO.setFieldType(confDO.getFieldType());
                                    dataDO.setFieldTextValue(value);
                                    dataDO.setFieldHashValue(HashUtil.mixHash(value));
                                    dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                                    templateGridDataDOS.add(dataDO);
                                }
                            }
                        }
                    }
                }
            }
            //主表系统字段值构建
            buildFormSystemDataFields(businessSubjectDataModel.getSubjectId(), rowId, templateFormDataDOS, templateFormConfDOS);

            Map<String, List<Long>> gridDataDOMaps = templateGridDataDOS.stream().collect(Collectors.groupingBy(TemplateGridDataDO::getGridTableName, Collectors.mapping(TemplateGridDataDO::getRowId, Collectors.toList())));

            //从表系统字段值构建
            for (Map.Entry<String, List<Long>> entry : gridDataDOMaps.entrySet()) {
                String gridTableName = entry.getKey();
                List<Long> gridRowIds = entry.getValue().stream().distinct().collect(Collectors.toList());
                for (Long gridRowId : gridRowIds) {
                    buildGridSystemDataFields(businessSubjectDataModel.getSubjectId(), rowId, gridRowId, gridTableName, templateGridDataDOS, templateGridConfDOS);
                }
            }

            if (templateFormDataDOS.size() > 0) {
                iTemplateFormDataService.saveBatch(templateFormDataDOS);
                log.info("保存业务主题表单数据完成!!!");
            }

            if (templateGridDataDOS.size() > 0) {
                iTemplateGridDataService.saveBatch(templateGridDataDOS);
                log.info("保存业务主题列表数据完成!!!");
            }
            //super.commit();
        } catch (Exception exception) {
            //super.rollback();
            log.error("保存业务主题表单数据异常! 异常原因:" + exception.getMessage());
            log.error("异常详细信息：" + exception);
            return "****保存业务主题表单数据失败!!****";
        }
        return "保存业务主题表单数据成功!!";
    }

    /**
     * 更新表单数据
     *
     * @param businessSubjectDataModel
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public String updateData(BusinessSubjectDataModel businessSubjectDataModel) {
        try {
            if (businessSubjectDataModel.getRowId() == null || businessSubjectDataModel.getRowId() == 0L) {
                throw new Exception("传入的数据行ID不允许为空！");
            }
            if (businessSubjectDataModel.getSubjectId() == null || businessSubjectDataModel.getSubjectId() == 0L) {
                throw new Exception("传入的业务主题ID不允许为空！");
            }
            QueryWrapper<TemplateFormDataDO> queryFormWrapper = new QueryWrapper<>();
            queryFormWrapper.eq("status", 1).eq("subject_id", businessSubjectDataModel.getSubjectId()).eq("row_id", businessSubjectDataModel.getRowId());

            //主表修改之前的数据
            List<TemplateFormDataDO> templateFormDataDOS = iTemplateFormDataService.list(queryFormWrapper);
            if (templateFormDataDOS == null || templateFormDataDOS.size() == 0) {
                throw new Exception("传入的数据行ID或业务主题ID无效！");
            }

            QueryWrapper<TemplateGridDataDO> queryGridWrapper = new QueryWrapper<>();
            queryGridWrapper.eq("status", 1).eq("subject_id", businessSubjectDataModel.getSubjectId()).eq("form_row_id", businessSubjectDataModel.getRowId());

            List<TemplateGridDataDO> templateGridDataDOS = iTemplateGridDataService.list(queryGridWrapper);
            if (templateGridDataDOS == null || templateGridDataDOS.size() == 0) {
                log.warn("------业务主题：{} 没有从表数据信息!", businessSubjectDataModel.getSubjectId());
            }

            // 根据业务主题ID获取表单配置信息
            List<TemplateFormConfDO> templateFormConfDOS = iTemplateFormConfService.getFieldsBySubjectId(businessSubjectDataModel.getSubjectId());
            if (CollectionUtils.isEmpty(templateFormConfDOS)) {
                throw new Exception("表单字段配置表中暂无该业务主题的相关配置！");
            }

            // 根据业务主题ID获取列表配置信息
            List<TemplateGridConfDO> templateGridConfDOS = iTemplateGridConfService.getFieldsBySubjectId(businessSubjectDataModel.getSubjectId());
            if (CollectionUtils.isEmpty(templateGridConfDOS)) {
                log.warn("------业务主题：{} 没有从表配置信息!", businessSubjectDataModel.getSubjectId());
            }

            JSONObject jsonObject = JSON.parseObject(businessSubjectDataModel.getDataJson());

            if (null == jsonObject) {
                throw new Exception("JSON解析失败！");
            }

            //主表数据
            List<TemplateFormDataDO> updateTemplateFormDataDOS = new ArrayList<>();

            //从表修改数据
            List<TemplateGridDataDO> updateTemplateGridDataDOS = new ArrayList<>();

            //从表删除数据
            List<TemplateGridDataDO> deleteTemplateGridDataDOS = new ArrayList<>();

            //从表新增数据
            List<TemplateGridDataDO> addTemplateGridDataDOS = new ArrayList<>();

            //遍历传入的JSON数据
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String fieldKey = entry.getKey();
                Object dataValue = entry.getValue();
                if (!(dataValue instanceof JSONArray)) {
                    //遍历主表业务主题字段
                    for (TemplateFormDataDO dataDO : templateFormDataDOS) {
                        if (dataDO.getFieldKey().equals(fieldKey)) {
                            //更新业务字段数据
                            String value = dataValue.toString();
                            dataDO.setFieldTextValue(value);
                            dataDO.setFieldHashValue(HashUtil.mixHash(value));
                            dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                            updateTemplateFormDataDOS.add(dataDO);
                        }
                    }
                } else if (fieldKey.startsWith("table_") && dataValue instanceof JSONArray) {
                    log.info("-->解析列表(table)数据, 表名：{}", fieldKey);
                    String tableName = fieldKey;
                    //如果是表格则将值转换成数组
                    JSONArray gridValues = (JSONArray) dataValue;
                    Long rowId = 0L;
                    //遍历从表业务主题字段
                    for (Object obj : gridValues) {
                        log.info("-->列表数据： {}", JSONObject.toJSONString(obj));
                        JSONObject gridObject = (JSONObject) obj;
                        rowId = gridObject.getString("rowId") != null ? Long.parseLong(gridObject.getString("rowId")) : 0L;
                        log.info("---------这里需要调整前端传入的数据结构，需要将列表数据的row_id、列名(field_key)传过来，才能作更新，否则不知道要更新哪些行、哪些列的数据。---------");
                        //从表数据行ID
                        Long subRowId = snowflakeUtil.nextId();
                        //遍历传入的JSON数据
                        for (Map.Entry<String, Object> grid : gridObject.entrySet()) {
                            String gridKey = grid.getKey();
                            Object gridValue = grid.getValue();
                            //如果当前行没有rowID或rowID等于0L，说明是新增加的数据行。
                            if (rowId == 0L) {
                                //遍历业务主题列表字段
                                for (TemplateGridConfDO confDO : templateGridConfDOS) {
                                    if (confDO.getFieldKey().equals(gridKey)) {
                                        TemplateGridDataDO dataDO = new TemplateGridDataDO();
                                        String column = confDO.getFieldName();
                                        String value = gridValue.toString();
                                        dataDO.setRowId(subRowId);
                                        dataDO.setFormRowId(businessSubjectDataModel.getRowId());
                                        dataDO.setEnterpriseId(confDO.getEnterpriseId());
                                        dataDO.setProjectId(confDO.getProjectId());
                                        dataDO.setSubjectId(confDO.getSubjectId());
                                        dataDO.setTemplateId(confDO.getId());
                                        dataDO.setFormTableId(confDO.getFormTableId());
                                        dataDO.setGridTableId(confDO.getGridTableId());
                                        dataDO.setGridTableName(tableName);
                                        dataDO.setFieldAlias(confDO.getFieldAlias());
                                        dataDO.setFieldKey(confDO.getFieldKey());
                                        dataDO.setFieldName(column);
                                        dataDO.setFieldType(confDO.getFieldType());
                                        dataDO.setFieldTextValue(value);
                                        dataDO.setFieldHashValue(HashUtil.mixHash(value));
                                        dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                                        addTemplateGridDataDOS.add(dataDO);
                                    }
                                }
                            } else {
                                if (gridKey.equals("rowId") && !rowId.equals(Long.parseLong(gridValue.toString()))) {
                                    rowId = Long.parseLong(gridValue.toString());
                                    continue;
                                }
                                //遍历业务主题列表字段
                                for (TemplateGridDataDO dataDO : templateGridDataDOS) {
                                    if (dataDO.getFieldKey().equals(gridKey) && dataDO.getRowId().equals(rowId)) {
                                        //更新业务字段值
                                        String value = gridValue.toString();
                                        dataDO.setFieldTextValue(value);
                                        dataDO.setFieldHashValue(HashUtil.mixHash(value));
                                        dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                                        updateTemplateGridDataDOS.add(dataDO);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //解析出已经删除的列表数据
            templateGridDataDOS.forEach(item -> {
                if (!updateTemplateGridDataDOS.stream().map(TemplateGridDataDO::getRowId).collect(Collectors.toList()).contains(item.getRowId())) {
                    //将数据状态设置为0-删除
                    item.setStatus(0);
                    deleteTemplateGridDataDOS.add(item);
                }
            });

            Map<String, List<Long>> gridDataDOMaps = addTemplateGridDataDOS.stream().collect(Collectors.groupingBy(TemplateGridDataDO::getGridTableName, Collectors.mapping(TemplateGridDataDO::getRowId, Collectors.toList())));

            //从表系统字段值构建(将数据字段的表名和行号取出)
            for (Map.Entry<String, List<Long>> entry : gridDataDOMaps.entrySet()) {
                String gridTableName = entry.getKey();
                List<Long> gridRowIds = entry.getValue().stream().distinct().collect(Collectors.toList());
                for (Long gridRowId : gridRowIds) {
                    buildGridSystemDataFields(businessSubjectDataModel.getSubjectId(), businessSubjectDataModel.getRowId(), gridRowId, gridTableName, addTemplateGridDataDOS, templateGridConfDOS);
                }
            }

            if (updateTemplateFormDataDOS.size() > 0) {
                iTemplateFormDataService.updateBatchById(updateTemplateFormDataDOS);
                log.info("更新业务主题【表单数据】完成!!!");
            }

            if (updateTemplateGridDataDOS.size() > 0) {
                iTemplateGridDataService.updateBatchById(updateTemplateGridDataDOS);
                log.info("更新业务主题【列表数据】完成!!!");
            }

            if (addTemplateGridDataDOS.size() > 0) {
                iTemplateGridDataService.saveBatch(addTemplateGridDataDOS);
                log.info("插入新增业务主题【列表数据】完成!!!");
            }

            if (deleteTemplateGridDataDOS.size() > 0) {
                iTemplateGridDataService.updateBatchById(deleteTemplateGridDataDOS);
                log.info("删除业务主题【列表数据】完成!!!");
            }

        } catch (Exception exception) {
            log.error("更新业务主题表单数据异常! 异常原因:" + exception.getMessage());
            log.error("异常详细信息：" + exception);
            return "****更新业务主题表单数据失败!!****";
        }

        return "更新数据完成！！！";
    }

    /**
     * 根据数据行号删除数据
     *
     * @param subjectId
     * @param rowId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public String deleteData(Long subjectId, Long rowId) {
        try {
            if (rowId == null || rowId == 0L) {
                throw new Exception("传入的数据行ID不允许为空！");
            }
            if (subjectId == null || subjectId == 0L) {
                throw new Exception("传入的业务主题ID不允许为空！");
            }
            QueryWrapper<TemplateFormDataDO> queryFormWrapper = new QueryWrapper<>();
            queryFormWrapper.eq("status", 1).eq("subject_id", subjectId).eq("row_id", rowId);

            //主表修改之前的数据
            List<TemplateFormDataDO> templateFormDataDOS = iTemplateFormDataService.list(queryFormWrapper);
            if (templateFormDataDOS == null || templateFormDataDOS.size() == 0) {
                throw new Exception("传入的数据行ID或业务主题ID无对应的数据记录！");
            }

            QueryWrapper<TemplateGridDataDO> queryGridWrapper = new QueryWrapper<>();
            queryGridWrapper.eq("status", 1).eq("subject_id", subjectId).eq("form_row_id", rowId);

            List<TemplateGridDataDO> templateGridDataDOS = iTemplateGridDataService.list(queryGridWrapper);
            if (templateGridDataDOS == null || templateGridDataDOS.size() == 0) {
                log.warn("------业务主题：{}, 主表数据行：{}, 没有从表数据信息!", subjectId, rowId);
            }

            //1. 先删除从表数据
            templateFormDataDOS.forEach(item -> item.setStatus(0));
            iTemplateFormDataService.updateBatchById(templateFormDataDOS);
            log.info("----删除主表数据完成, 影响数据：{} 行!!!---", templateFormDataDOS.size());

            //2. 再删除主表数据
            templateGridDataDOS.forEach(item -> item.setStatus(0));
            iTemplateGridDataService.updateBatchById(templateGridDataDOS);
            log.info("----删除从表数据完成, 影响数据：{} 行!!!---", templateGridDataDOS.size());
        } catch (Exception exception) {
            log.error("删除业务主题表单数据异常! 异常原因:" + exception.getMessage());
            log.error("异常详细信息：" + exception);
            return "****删除业务主题表单数据失败!!****";
        }

        return null;
    }

    /**
     * 构建主表系统字段
     *
     * @param subjectId
     * @param rowId
     * @param templateFormDataDOS
     * @param templateFormConfDOS
     */
    private void buildFormSystemDataFields(Long subjectId, Long rowId, List<TemplateFormDataDO> templateFormDataDOS, List<TemplateFormConfDO> templateFormConfDOS) {
        String[] columns = {"id", "status"};
        //添加系统字段数据
        for (SystemFieldEnum systemFieldEnum : getSystemFieldEnum()) {
            String column = systemFieldEnum.getFieldName().toLowerCase();
            String value = null;

            //排除ID和STATUS字段
            if (Arrays.asList(columns).contains(column)) {
                continue;
            }

            switch (systemFieldEnum) {
                case UPDATE_TIME:
                case CREATE_TIME: {
                    value = DateUtil.format(BaseInfoContextHolder.getSystemInfo().getUpdTime(), "yyyy-MM-dd HH:mm:ss");
                    break;
                }
                case LAST_OPERATOR:
                case CREATOR: {
                    value = BaseInfoContextHolder.getSystemInfo().getUserName();
                    break;
                }
                case LAST_OPERATOR_ID:
                case CREATOR_ID: {
                    value = BaseInfoContextHolder.getSystemInfo().getUserId().toString();
                    break;
                }
                case CREATED_HOST_IP:
                case UPDATE_HOST_IP: {
                    value = BaseInfoContextHolder.getSystemInfo().getUserIp();
                    break;
                }
            }

            //找到当前系统字段在配置表中的信息
            Optional<TemplateFormConfDO> optionalTemplateFormConfDO = templateFormConfDOS.stream().filter(k -> k.getFieldKey().equals(column)).findFirst();
            if (optionalTemplateFormConfDO.isPresent()) {
                //添加主表系统字段数据
                TemplateFormDataDO dataDO = new TemplateFormDataDO();
                dataDO.setRowId(rowId);
                dataDO.setEnterpriseId(optionalTemplateFormConfDO.get().getEnterpriseId());
                dataDO.setProjectId(optionalTemplateFormConfDO.get().getProjectId());
                dataDO.setSubjectId(subjectId);
                dataDO.setTemplateId(optionalTemplateFormConfDO.get().getId());
                dataDO.setTableId(optionalTemplateFormConfDO.get().getTableId());
                dataDO.setTableName(optionalTemplateFormConfDO.get().getTableName());
                dataDO.setFieldAlias(systemFieldEnum.getFieldAlias());
                dataDO.setFieldKey(systemFieldEnum.getFieldName());
                dataDO.setFieldName(column);
                dataDO.setFieldType(systemFieldEnum.getFieldType());
                dataDO.setFieldTextValue(value);
                dataDO.setFieldHashValue(HashUtil.mixHash(value));
                dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                templateFormDataDOS.add(dataDO);
            }
        }
    }

    /**
     * 构建从表系统字段
     *
     * @param subjectId
     * @param rowId
     * @param subRowId
     * @param gridTableName
     * @param templateGridDataDOS
     * @param templateGridConfDOS
     */
    private void buildGridSystemDataFields(Long subjectId, Long rowId, Long subRowId, String gridTableName, List<TemplateGridDataDO> templateGridDataDOS, List<TemplateGridConfDO> templateGridConfDOS) {
        String[] columns = {"id", "status"};
        //添加系统字段数据
        for (SystemFieldEnum systemFieldEnum : getSystemFieldEnum()) {
            String column = systemFieldEnum.getFieldName().toLowerCase();
            String value = null;

            //排除ID和STATUS字段
            if (Arrays.asList(columns).contains(column)) {
                continue;
            }

            switch (systemFieldEnum) {
                case UPDATE_TIME:
                case CREATE_TIME: {
                    value = DateUtil.format(BaseInfoContextHolder.getSystemInfo().getUpdTime(), "yyyy-MM-dd HH:mm:ss");
                    break;
                }
                case LAST_OPERATOR:
                case CREATOR: {
                    value = BaseInfoContextHolder.getSystemInfo().getUserName();
                    break;
                }
                case LAST_OPERATOR_ID:
                case CREATOR_ID: {
                    value = BaseInfoContextHolder.getSystemInfo().getUserId().toString();
                    break;
                }
                case CREATED_HOST_IP:
                case UPDATE_HOST_IP: {
                    value = BaseInfoContextHolder.getSystemInfo().getUserIp();
                    break;
                }
            }

            //找到当前系统字段在配置表中的信息
            Optional<TemplateGridConfDO> optionalTemplateGridConfDO = templateGridConfDOS.stream().filter(k -> k.getFieldKey().equals(column) && k.getGridTableName().equals(gridTableName)).findFirst();
            if (optionalTemplateGridConfDO.isPresent()) {
                //添加从表系统字段数据
                TemplateGridDataDO dataDO = new TemplateGridDataDO();
                dataDO.setRowId(subRowId);
                dataDO.setFormRowId(rowId);
                dataDO.setEnterpriseId(optionalTemplateGridConfDO.get().getEnterpriseId());
                dataDO.setProjectId(optionalTemplateGridConfDO.get().getProjectId());
                dataDO.setSubjectId(subjectId);
                dataDO.setTemplateId(optionalTemplateGridConfDO.get().getId());
                dataDO.setFormTableId(optionalTemplateGridConfDO.get().getFormTableId());
                dataDO.setGridTableId(optionalTemplateGridConfDO.get().getGridTableId());
                dataDO.setGridTableName(optionalTemplateGridConfDO.get().getGridTableName());
                dataDO.setFieldAlias(optionalTemplateGridConfDO.get().getFieldAlias());
                dataDO.setFieldKey(optionalTemplateGridConfDO.get().getFieldKey());
                dataDO.setFieldName(column);
                dataDO.setFieldType(optionalTemplateGridConfDO.get().getFieldType());
                dataDO.setFieldTextValue(value);
                dataDO.setFieldHashValue(HashUtil.mixHash(value));
                dataDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
                templateGridDataDOS.add(dataDO);
            }
        }
    }

    /**
     * 检查传入的数据结构
     *
     * @throws Exception
     */
    private void checkValidate(BusinessSubjectDataModel businessSubjectDataModel) throws Exception {
        if (StringUtils.isBlank(businessSubjectDataModel.getDataJson())) {
            throw new Exception("传入的JSON不允许为空!");
        }
        if (null == businessSubjectDataModel.getStatus()) {
            throw new Exception("传入的数据状态不允许为空!");
        }
        if (businessSubjectDataModel.getSubjectId() == null) {
            throw new Exception("传入的业务主题ID不允许为空!");
        }
    }

    /**
     * 查询表单数据(主表+从表,行转列)
     *
     * @param subjectId
     * @return
     */
    @Override
    public List<FormDataInfoModel> queryFormDataList(Long subjectId) {
        int formCounts = 0;
        int gridCounts = 0;
        int gridTables = 0;

        QueryWrapper<TemplateFormDataDO> queryFormWrapper = new QueryWrapper<>();
        queryFormWrapper.eq("status", 1).eq("subject_id", subjectId);
        List<TemplateFormDataDO> formDataDOS = iTemplateFormDataService.list(queryFormWrapper);

        if (formDataDOS != null && formDataDOS.size() > 0) {
            Map<Object, Long> collectForm = formDataDOS.stream().collect(Collectors.groupingBy(r -> r.getRowId(), Collectors.counting()));
            formCounts = collectForm.size();

            log.info("-->主表数据：{}", JSONObject.toJSONString(formDataDOS));
            log.info("-->主表行数：{}", formCounts);
        }

        QueryWrapper<TemplateGridDataDO> queryGridWrapper = new QueryWrapper<>();
        queryGridWrapper.eq("status", 1).eq("subject_id", subjectId);
        List<TemplateGridDataDO> gridDataDOS = iTemplateGridDataService.list(queryGridWrapper);

        if (gridDataDOS != null && gridDataDOS.size() > 0) {
            Map<Object, Long> collectGrid = gridDataDOS.stream().collect(Collectors.groupingBy(r -> r.getRowId(), Collectors.counting()));
            gridCounts = collectGrid.size();

            Map<Object, Long> collectTable = gridDataDOS.stream().collect(Collectors.groupingBy(r -> r.getGridTableId(), Collectors.counting()));
            gridTables = collectTable.size();

            log.info("-->从表数据：{}", JSONObject.toJSONString(gridDataDOS));
            log.info("-->从表数量：{}", JSONObject.toJSONString(gridTables));
            log.info("-->从表行数：{}", JSONObject.toJSONString(gridCounts));
        }

        try {
            List<List<Object>> formList = RowConvertColUtil.doConvert(formDataDOS, "fieldKey", "enterpriseId", "fieldTextValue", true);
            for (List<Object> list : formList) {
                log.info("主表（行转列）当前行数据:{}", list.toString());
            }

            List<List<Object>> gridList = RowConvertColUtil.doConvert(gridDataDOS, "fieldKey", "enterpriseId", "fieldTextValue", true);
            for (List<Object> list : gridList) {
                log.info("从表（行转列）当前行数据:{}", list.toString());
            }
        } catch (Exception exception) {
            log.error("-->行转列异常, 异常信息:,{}", exception.getMessage());
        }

        List<FormDataInfoModel> result = new ArrayList<>();

        for (TemplateFormDataDO formDataDO : formDataDOS) {
            List<GridDataInfoModel> gridDataInfoModels = new ArrayList<>();
            List<TemplateGridDataDO> currentGridDatas = gridDataDOS.stream().filter(o -> o.getFormRowId().equals(formDataDO.getRowId())).collect(Collectors.toList());
            if (currentGridDatas != null && currentGridDatas.size() > 0) {
                for (TemplateGridDataDO currentGridDataDO : currentGridDatas) {
                    GridDataInfoModel gridDataInfoModel = new GridDataInfoModel();
                    BeanCopier gridBeanCopier = BeanCopier.create(TemplateGridDataDO.class, GridDataInfoModel.class, false);
                    gridBeanCopier.copy(currentGridDataDO, gridDataInfoModel, null);
                    gridDataInfoModels.add(gridDataInfoModel);
                }
            }

            FormDataInfoModel formDataInfoModel = new FormDataInfoModel();
            BeanCopier formBeanCopier = BeanCopier.create(TemplateFormDataDO.class, FormDataInfoModel.class, false);
            formBeanCopier.copy(formDataDO, formDataInfoModel, null);

            formDataInfoModel.setGridDataInfoModels(gridDataInfoModels);
            result.add(formDataInfoModel);
        }
        return result;
    }

    /**
     * 查询表单数据(行转列-仅主表数据)
     *
     * @param subjectId
     * @return
     */
    @Override
    public DynamicDataVO queryFormDataMaps(Long subjectId) {

        //获取业务主题配置的字段信息
        List<TemplateFormConfDO> formConfDOS = iTemplateFormConfService.getFieldsBySubjectId(subjectId);

        if (CollectionUtils.isEmpty(formConfDOS)) {
            return null;
        }

        //展示的字段表头
        List<DynamicFieldsVO> fieldsVOS = new ArrayList<>();
        for (TemplateFormConfDO confDO : formConfDOS) {
            DynamicFieldsVO header = new DynamicFieldsVO();
            header.setFieldId(confDO.getId());
            header.setFieldAlias(confDO.getFieldAlias());
            header.setFieldKey(confDO.getFieldKey());
            header.setFieldName(confDO.getFieldName());
            header.setFieldType(confDO.getFieldType());
            header.setFieldOrder(confDO.getFieldOrder());
            header.setTableName("t_template_form_data_".concat(String.valueOf(BaseInfoContextHolder.getEnterpriseAndProjectInfo().getEnterpriseId())));

            fieldsVOS.add(header);
        }

        int formCounts = 0;
        int gridCounts = 0;
        int gridTables = 0;

        QueryWrapper<TemplateFormDataDO> queryFormWrapper = new QueryWrapper<>();
        queryFormWrapper.eq("status", 1).eq("subject_id", subjectId);
        List<Map<String, Object>> formDataDOS = iTemplateFormDataService.listMaps(queryFormWrapper);

        if (CollectionUtils.isNotEmpty(formDataDOS)) {
            Map<Object, Long> collectForm = formDataDOS.stream().collect(Collectors.groupingBy(r -> r.get("rowId"), Collectors.counting()));
            formCounts = collectForm.size();

            log.info("-->主表数据：{}", JSONObject.toJSONString(formDataDOS));
            log.info("-->主表行数：{}", formCounts);

            QueryWrapper<TemplateGridDataDO> queryGridWrapper = new QueryWrapper<>();
            queryGridWrapper.eq("status", 1).eq("subject_id", subjectId).eq("form_row_id", formDataDOS.get(0).get("rowId"));
            List<Map<String, Object>> gridDataDOS = iTemplateGridDataService.listMaps(queryGridWrapper);

            if (CollectionUtils.isNotEmpty(gridDataDOS)) {
                Map<Object, Long> collectGrid = gridDataDOS.stream().collect(Collectors.groupingBy(r -> r.get("rowId"), Collectors.counting()));
                gridCounts = collectGrid.size();

                Map<Object, Long> collectTable = gridDataDOS.stream().collect(Collectors.groupingBy(r -> r.get("gridTableId"), Collectors.counting()));
                gridTables = collectTable.size();

                log.info("-->从表数据：{}", JSONObject.toJSONString(gridDataDOS));
                log.info("-->从表数量：{}", JSONObject.toJSONString(gridTables));
                log.info("-->从表行数：{}", JSONObject.toJSONString(gridCounts));
            }
        }
        DynamicDataVO result = new DynamicDataVO();
        result.setHeader(fieldsVOS);
        result.setRows(formDataDOS);
        result.setTotalRows(Integer.toUnsignedLong(formCounts));
        return result;
    }

    /**
     * 获取指定表单数据明细（包括从表数据）
     *
     * @param subjectId
     * @param rowId
     * @return
     */
    @Override
    public Map<String, Object> getDetail(Long subjectId, Long rowId) {
        int formCounts = 0;
        int gridCounts = 0;
        int gridTables = 0;

        Map<String, Object> result = new HashMap<>();
        QueryWrapper<TemplateFormDataDO> queryFormWrapper = new QueryWrapper<>();
        queryFormWrapper.eq("status", 1).eq("subject_id", subjectId).eq("row_id", rowId);

        //查询主表记录
        List<Map<String, Object>> formDataDOS = iTemplateFormDataService.listMaps(queryFormWrapper);
        if (CollectionUtils.isNotEmpty(formDataDOS)) {
            Map<Object, Long> collectForm = formDataDOS.stream().collect(Collectors.groupingBy(r -> r.get("rowId"), Collectors.counting()));
            formCounts = collectForm.size();

            log.info("-->主表数据：{}", JSONObject.toJSONString(formDataDOS));
            log.info("-->主表行数：{}", formCounts);

            result.put("form_table",formDataDOS);


            QueryWrapper<TemplateGridDataDO> queryGridWrapper = new QueryWrapper<>();
            queryGridWrapper.eq("status", 1).eq("subject_id", subjectId).eq("form_row_id", formDataDOS.get(0).get("rowId"));
            //查询从表数据
            List<Map<String, Object>> gridDataDOS = iTemplateGridDataService.listMaps(queryGridWrapper);

            if (CollectionUtils.isNotEmpty(gridDataDOS)) {
                Map<Object, Long> collectGrid = gridDataDOS.stream().collect(Collectors.groupingBy(r -> r.get("rowId"), Collectors.counting()));
                gridCounts = collectGrid.size();

                Map<Object, Long> collectTable = gridDataDOS.stream().collect(Collectors.groupingBy(r -> r.get("gridTableId"), Collectors.counting()));
                gridTables = collectTable.size();

                log.info("-->从表数据：{}", JSONObject.toJSONString(gridDataDOS));
                log.info("-->从表数量：{}", JSONObject.toJSONString(gridTables));
                log.info("-->从表行数：{}", JSONObject.toJSONString(gridCounts));

                result.put("grid_table", gridDataDOS);
            }
        }
        return result;
    }
}
