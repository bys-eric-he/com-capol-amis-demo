package com.capol.amis.helpers.gen;

import com.capol.amis.entity.DatasetUnionDO;
import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.entity.bo.DatasetRightUnionBO;
import com.capol.amis.entity.bo.DatasetTableBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.entity.bo.DatasetUnionFieldBO;
import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.service.IDatasetDataService;
import com.capol.amis.utils.BaseInfoContextHolder;
import com.capol.amis.utils.SnowflakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:31
 * desc: 测试类实体生成
 */
@Component
public class TestEntityGenerator {

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Autowired
    private TemplateFormDataMapper templateFormDataMapper;

    @Autowired
    private IDatasetDataService datasetDataService;

    public DatasetUnionDO buildUnionDO(long leftDataId, long rightDataId, long datasetId) {
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

    public DatasetUnionBO getDatasetUnion() {
        List<DatasetUnionBO> unionFields = datasetDataService.getUnionFields();
        return unionFields == null ? null : unionFields.get(0);
    }

    public List<Map<String, Object>> getMapList(int n) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(snowflakeUtil.nextId()));
            map.put("age", String.valueOf(i + ThreadLocalRandom.current().nextInt(30) + 20));
            map.put("creator", "zhangsan");
            map.put("creator_id", String.valueOf(100000000+ThreadLocalRandom.current().nextInt(20000) + 20));
            list.add(map);
        }
        return list;
    }
}
