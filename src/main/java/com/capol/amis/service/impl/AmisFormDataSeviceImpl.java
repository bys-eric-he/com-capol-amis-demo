package com.capol.amis.service.impl;

import cn.hutool.core.util.HashUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.entity.TemplateGridConfDO;
import com.capol.amis.entity.TemplateGridDataDO;
import com.capol.amis.model.BusinessSubjectDataModel;
import com.capol.amis.service.*;
import com.capol.amis.service.transaction.ServiceTransactionDefinition;
import com.capol.amis.utils.BaseInfoContextHolder;
import com.capol.amis.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表单数据录入服务类
 */
@Slf4j
@Service
public class AmisFormDataSeviceImpl extends ServiceTransactionDefinition implements IAmisFormDataSevice {
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
    @Override
    public String insertData(BusinessSubjectDataModel businessSubjectDataModel) {
        super.start();
        try {
            checkValidate(businessSubjectDataModel);
            // 根据业务主题ID获取表单配置信息
            List<TemplateFormConfDO> templateFormConfDOS = iTemplateFormConfService.getFieldsBySubjectId(businessSubjectDataModel.getSubjectId());
            if (CollectionUtils.isEmpty(templateFormConfDOS)) {
                throw new Exception("表单字段配置表中暂无该业务主题的相关配置！");
            }

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
                            TemplateFormDataDO dataDO = new TemplateFormDataDO();
                            String column = confDO.getFieldName();
                            String value = dataValue.toString();
                            dataDO.setRowId(rowId);
                            dataDO.setEnterpriseId(confDO.getEnterpriseId());
                            dataDO.setProjectId(confDO.getProjectId());
                            dataDO.setSubjectId(confDO.getSubjectId());
                            dataDO.setTemplateId(confDO.getId());
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
                    log.info("解析列表(table)数据.....");
                    //如果是表格则将值转换成数组
                    JSONArray gridValues = (JSONArray) dataValue;
                    //遍历从表业务主题字段
                    for (Object obj : gridValues) {
                        log.info("列表数据： {}", JSONObject.toJSONString(obj));
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

            if (templateFormDataDOS.size() > 0) {
                iTemplateFormDataService.saveBatch(templateFormDataDOS);
                log.info("保存业务主题表单数据完成!!!");
            }

            if (templateGridDataDOS.size() > 0) {
                iTemplateGridDataService.saveBatch(templateGridDataDOS);
                log.info("保存业务主题列表数据完成!!!");
            }
            super.commit();
        } catch (Exception exception) {
            super.rollback();
            log.error("保存业务主题表单数据异常! 异常原因:" + exception.getMessage());
            log.error("异常详细信息：" + exception);
            return "****保存业务主题表单数据失败!!****";
        }
        return "保存业务主题表单数据成功!!";
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

}
