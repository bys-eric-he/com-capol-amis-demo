package com.capol.amis.mapper;

import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.FormFieldConfigDO;
import com.capol.amis.enums.TableSourceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @Test
    public void testMapper() {

    }

}
