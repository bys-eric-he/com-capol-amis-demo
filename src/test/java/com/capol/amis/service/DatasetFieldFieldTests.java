package com.capol.amis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.DatasetFieldDO;
import com.capol.amis.entity.DatasetUnionDO;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.enums.TableFieldTypeEnum;
import com.capol.amis.enums.TableSourceTypeEnum;
import com.capol.amis.helpers.gen.TestEntityGenerator;
import com.capol.amis.mapper.DatasetUnionMapper;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.utils.BaseInfoContextHolder;
import com.capol.amis.utils.SnowflakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/21 09:14
 */
public class DatasetFieldFieldTests extends AmisApplicationTests {

    @Autowired
    private ITemplateFormConfService templateFormConfService;

    @Autowired
    private IDatasetFieldService datasetFieldService;

    @Autowired
    private TemplateFormDataMapper templateFormDataMapper;

    @Autowired
    private DatasetUnionMapper datasetUnionMapper;

    @Autowired
    private ITemplateFormDataService templateFormDataService;

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Autowired
    private TestEntityGenerator testEntityGenerator;

    @Test
    public void testInsertDatasetUnion() {
        long datasetId = 331622895329476608L;
        long leftDataId = 328445634593947689L;
        long rightDataId = 328467342231076882L;
        DatasetUnionDO datasetUnionDO = testEntityGenerator.buildUnionDO(leftDataId, rightDataId, datasetId);
        datasetUnionMapper.insert(datasetUnionDO);
    }

    private DatasetUnionDO buildUnionBO(long leftDataId, long rightDataId, long datasetId) {
        TemplateFormDataDO source = templateFormDataMapper.selectById(leftDataId);
        TemplateFormDataDO target = templateFormDataMapper.selectById(rightDataId);
        DatasetUnionDO unionDO = new DatasetUnionDO();
        unionDO.setId(snowflakeUtil.nextId());
        unionDO.setDatasetId(datasetId);

        // 左表
        unionDO.setSourceSubjectKey(source.getFieldKey());
        unionDO.setSourceSubjectId(source.getSubjectId());
        unionDO.setSourceTableId(source.getTableId());
        unionDO.setSourceTableName(source.getTableName());
        unionDO.setSourceFieldId(source.getTemplateId());
        // 右表
        unionDO.setTargetSubjectKey(target.getFieldKey());
        unionDO.setTargetSubjectId(target.getSubjectId());
        unionDO.setTargetTableId(target.getTableId());
        unionDO.setTargetTableName(target.getTableName());
        unionDO.setTargetFieldId(target.getTemplateId());
        unionDO.setTargetTableNameAlias("t_1");

        unionDO.setUnionType(1);

        unionDO.setOrderNo(1);
        unionDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
        return unionDO;
    }

    @Test
    public void testInsertDatasetField() {
        int col = 0;
        long datasetId = snowflakeUtil.nextId();
        List<DatasetFieldDO> datasetFields = new ArrayList<>();
        // 左表基本信息
        List<TemplateFormConfDO> leftConfs = getConfsByTableId(328434438381764608L);
        // 右表基本信息
        List<TemplateFormConfDO> rightConfs = getConfsByTableId(328465748152287232L);

        col = buildDatasetField(leftConfs, datasetId, col, "t_0", "subjectKey_0", datasetFields);
        buildDatasetField(rightConfs, datasetId, col, "t_1", "subjectKey_1", datasetFields);
        //datasetFields.forEach(System.out::println);
        datasetFieldService.saveBatch(datasetFields);
    }

    private List<TemplateFormConfDO> getConfsByTableId(long tableId) {
        return templateFormConfService
                .list(new LambdaQueryWrapper<TemplateFormConfDO>().eq(TemplateFormConfDO::getTableId, tableId));
    }

    private int buildDatasetField(List<TemplateFormConfDO> leftConfs,
                                  long datasetId, int col, String tableNameAlias, String subjectKey,
                                  List<DatasetFieldDO> datasetFields) {
        for (TemplateFormConfDO formConf : leftConfs) {
            DatasetFieldDO datasetFieldDO = new DatasetFieldDO();
            BeanUtils.copyProperties(formConf, datasetFieldDO);
            datasetFieldDO.setId(snowflakeUtil.nextId());
            datasetFieldDO.setDatasetId(datasetId);
            datasetFieldDO.setTableSourceType(TableSourceTypeEnum.USER_DIFINE_TABLE.getCode());
            datasetFieldDO.setFieldNameAlias("f_" + (++col));
            datasetFieldDO.setSubjectKey(subjectKey);
            datasetFieldDO.setFieldId(formConf.getId());
            datasetFieldDO.setTableNameAlias(tableNameAlias);
            datasetFieldDO.setFieldTypeNum(TableFieldTypeEnum.TEXT.getTypeCode());
            datasetFieldDO.setShowType(1);
            datasetFieldDO.setOriFieldType(formConf.getFieldType());
            datasetFieldDO.setSystemInfo(BaseInfoContextHolder.getSystemInfo());
            datasetFields.add(datasetFieldDO);
        }
        return col;
    }

}
