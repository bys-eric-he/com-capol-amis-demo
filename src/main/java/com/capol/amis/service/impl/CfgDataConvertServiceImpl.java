package com.capol.amis.service.impl;

import cn.hutool.core.lang.hash.CityHash;
import com.capol.amis.entity.FormFieldConfigDO;
import com.capol.amis.entity.FormFieldConfigExtDO;
import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.enums.TableFieldTypeEnum;
import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.enums.TableSourceTypeEnum;
import com.capol.amis.mapper.CfgDynamicDataMapper;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.service.ICfgDataConvertService;
import com.capol.amis.service.ITemplateFormDataService;
import com.capol.amis.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Yaxi.Zhang
 * @since 2022/6/29 19:29
 */
@Service
@Slf4j
public class CfgDataConvertServiceImpl implements ICfgDataConvertService {
    @Autowired
    private CfgDynamicDataMapper cfgDynamicDataMapper;

    @Autowired
    private TemplateFormDataMapper templateFormDataMapper;

    @Autowired
    private ITemplateFormDataService templateFormDataService;

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Override
    public void transformData() {
        // 列出所有自定义类型的表的subject_id
        // (fields) => (tbl, fields)
        List<FormFieldConfigExtDO> fieldList = cfgDynamicDataMapper.getFieldList(TableSourceTypeEnum.USER_DIFINE_TABLE.getCode());
        Map<String, List<FormFieldConfigExtDO>> tblMap = fieldList.stream()
                .collect(Collectors.groupingBy(FormFieldConfigDO::getTableName));
        tblMap.forEach((tbl, fields) -> {
            // fields -> (field_name, field_info)
            Map<String, FormFieldConfigExtDO> fieldMap = new HashMap<>();
            fields.forEach(field -> fieldMap.put(field.getFieldName(), field));
            List<TemplateFormDataDO> transferredData = transformDataByTblName(tbl, fieldMap);
            // TODO 插入到新表中!!!!
            // if (CollectionUtils.isNotEmpty(transferredData)) {
            //     templateFormDataService.saveBatch(transferredData);
            // }
        });
    }

    @Override
    public void transformData4Test(Integer limit) {
        // 列出所有自定义类型的表的subject_id
        // (fields) => (tbl, fields)
        List<FormFieldConfigExtDO> fieldList = cfgDynamicDataMapper.getFieldList(TableSourceTypeEnum.USER_DIFINE_TABLE.getCode());
        Map<String, List<FormFieldConfigExtDO>> tblMap = fieldList.stream()
                .collect(Collectors.groupingBy(FormFieldConfigDO::getTableName));

        for (String tbl : getRandSizeSet(tblMap.keySet(), limit)) {
            List<FormFieldConfigExtDO> fields = tblMap.get(tbl);
            Map<String, FormFieldConfigExtDO> fieldMap = new HashMap<>();
            fields.forEach(field -> fieldMap.put(field.getFieldName(), field));
            List<TemplateFormDataDO> transferredData = transformDataByTblName(tbl, fieldMap);
            if (CollectionUtils.isNotEmpty(transferredData)) {
                transferredData.forEach(data -> log.info("=========>>>>> {}", data));
            }
        }
    }

    private Set<String> getRandSizeSet(Set<String> origin, int size) {
        if (CollectionUtils.isEmpty(origin) || size >= origin.size()) {
            return origin;
        } else {
            List<String> list = new ArrayList<>(origin);
            Set<String> res = new HashSet<>();
            for (int i = 0; i < size; i++) {
                int rand = ThreadLocalRandom.current().nextInt(list.size());
                res.add(list.get(rand));
                list.remove(rand);
            }
            return res;
        }
    }

    /**
     * 依据表名转换数据
     */
    private List<TemplateFormDataDO> transformDataByTblName(String tbl, Map<String, FormFieldConfigExtDO> fieldMap) {
        // 查动态表数据
        List<TemplateFormDataDO> transferredData = null;
        List<Map<String, Object>> listMapDatas = cfgDynamicDataMapper.getListMapDatas(tbl);
        // 动态表字段获取
        if (CollectionUtils.isNotEmpty(listMapDatas)) {
            for (Map<String, Object> dataMap : listMapDatas) {
                // dataMap -> (field_name, field_value)
                transferredData = buildTemplateFormDataDO(dataMap, fieldMap);
            }
        }
        return transferredData;
    }

    /**
     * 数据行转列
     * @param dataMap 一行数据 (field, value)
     * @param fieldMap 字段信息
     */
    private List<TemplateFormDataDO> buildTemplateFormDataDO(Map<String, Object> dataMap, Map<String, FormFieldConfigExtDO> fieldMap) {
        List<TemplateFormDataDO> cfgForm = new LinkedList<>();
        dataMap.forEach((filed, value) -> {
            FormFieldConfigDO fieldConfig = fieldMap.get(filed);
            // 只处理主表字段
            if (fieldConfig.getTableRelationType().equals(TableRelationTypeEnum.MAIN_TYPE.getValue())) {
                TemplateFormDataDO tfd = new TemplateFormDataDO();
                // 基础信息
                BeanUtils.copyProperties(fieldConfig, tfd);
                tfd.setId(snowflakeUtil.nextId());
                tfd.setTemplateId(fieldConfig.getId());
                // 字段和字段值
                tfd.setFieldName(filed);
                TableFieldTypeEnum fieldType = TableFieldTypeEnum.getFieldTypeEnumByDesc(fieldConfig.getFieldType());
                if (value != null) {
                    tfd.setFieldHashValue(CityHash.hash64(value.toString().getBytes(StandardCharsets.UTF_8)));
                    switch (fieldType) {
                        case TINYINT:
                        case BIGINT:
                            tfd.setFieldNumberValue(Long.parseLong(value.toString()));
                            break;
                        case VARCHAR:
                            tfd.setFieldStringValue(value.toString());
                            break;
                        case TEXT:
                            tfd.setFieldTextValue(value.toString());
                            break;
                        case DATETIME:
                            Date date = (Date) value;
                            tfd.setFieldStringValue(date.toString());
                        default:
                            break;
                    }
                }
                cfgForm.add(tfd);
            }
        });
        return cfgForm;
    }

}
