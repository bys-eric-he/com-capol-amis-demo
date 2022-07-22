package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.CfgFormDictDO;
import com.capol.amis.mapper.CfgFormDictMapper;
import com.capol.amis.service.ICfgFormDictService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 表单字典表 服务实现类
 * </p>
 *
 * @author He.Yong
 * @since 2022-07-21
 */
@Service
public class CfgFormDictServiceImpl extends ServiceImpl<CfgFormDictMapper, CfgFormDictDO>
        implements ICfgFormDictService {

    @Autowired
    private CfgFormDictMapper cfgFormDictMapper;

    /**
     * 根据表名获取该表配置的字典字段信息
     *
     * @param tableName
     * @return
     */
    @Override
    public List<CfgFormDictDO> getDictByTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        return cfgFormDictMapper.getDictByTableName(tableName);
    }

    /**
     * 从字典表中获取展示字段值
     *
     * @param formDictDOS
     * @param fieldShowName
     * @param dictValue
     * @return
     */
    @Override
    public String getDictLabel(List<CfgFormDictDO> formDictDOS, String fieldShowName, String dictValue) {
        if (StringUtils.isBlank(fieldShowName) || StringUtils.isBlank(dictValue) || CollectionUtils.isEmpty(formDictDOS)) {
            return null;
        }

        String fieldName = fieldShowName.replace("_show", "");
        if (dictValue.contains(",")) {
            String[] valueList = dictValue.split(",");
            StringBuffer sb = new StringBuffer();
            List<CfgFormDictDO> valueDictDOs = formDictDOS.stream().filter(item -> fieldName.equals(item.getFieldName())).collect(Collectors.toList());
            Map<String, String> valueMap = valueDictDOs.stream().collect(Collectors.toMap(CfgFormDictDO::getDictValue, CfgFormDictDO::getDictLabel));
            for (String value : valueList) {
                sb.append(valueMap.get(value)).append(",");
            }
            String label = sb.toString().substring(0, sb.length() - 1);
            return label;
        } else {
            List<CfgFormDictDO> valueDictDOs = formDictDOS.stream().filter(item -> dictValue.equals(item.getDictValue())
                    && fieldName.equals(item.getFieldName())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(valueDictDOs)) {
                return valueDictDOs.get(0).getDictLabel();
            }
        }

        return null;

    }
}
