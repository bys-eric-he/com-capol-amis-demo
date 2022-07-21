package com.capol.amis.service;

import com.capol.amis.AmisApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Yaxi.Zhang
 * @since 2022/6/30 20:06
 */
@Slf4j
public class CfgDataConvertServiceTests extends AmisApplicationTests {
    @Autowired
    private ICfgDataConvertService cfgDataConvertService;

    @Test
    public void testTransformData() {
        cfgDataConvertService.transformData4Test(20);
    }


}
