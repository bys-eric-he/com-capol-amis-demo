package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.capol.amis.AmisApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yaxi.Zhang
 * @since 2022/6/30 11:25
 */
@Slf4j
public class MapperTests extends AmisApplicationTests {
    @Autowired
    private CfgDynamicDataMapper cfgDynamicDataMapper;

    @Autowired
    private FormFieldConfigMapper formFieldConfigMapper;

    @Autowired
    private QaBusinessSubjectMapper qaBusinessSubjectMapper;

    @Autowired
    private BusinessSubjectMapper businessSubjectMapper;

    @Autowired
    private TemplateFormConfMapper templateFormConfMapper;

    @Autowired
    private TemplateGridConfMapper templateGridConfMapper;


    @Test
    public void testMapper() {
        List<Map<String, Object>> maps = formFieldConfigMapper.selectMaps(new QueryWrapper<>());
        maps.forEach(System.out::println);
        /*List<Map<String, Object>> datas = cfgDynamicDataMapper.getListMapDatas("cfg_table_1674397734739969");
        System.out.println(datas);*/

        /*formFieldConfigMapper.getTblsFromSubjectId(1744575823925249L).forEach(System.out::println);

        List<BusinessSubjectDO> businessSubjectDOS = businessSubjectMapper.selectList(new QueryWrapper<>());
        System.out.println(businessSubjectDOS);*/

        //qaBusinessSubjectMapper.selectList(new QueryWrapper<>()).forEach(System.out::println);
    }

    @Test
    public void testMapper1() {
        Set<Long> templateTableIds = templateFormConfMapper.selectDistinctTableIds();
        System.out.println("list = " + templateTableIds);
        Set<Long> gridTableIds = templateGridConfMapper.selectDistinctTableIds();
        System.out.println("gridTableIds = " + gridTableIds);
    }

}
