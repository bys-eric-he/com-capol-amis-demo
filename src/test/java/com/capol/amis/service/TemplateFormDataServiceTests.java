package com.capol.amis.service;


import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.bo.TemplateDataBO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/13 11:18
 */
public class TemplateFormDataServiceTests extends AmisFormDataSeviceTests {
    @Autowired
    ITemplateFormDataService templateFormDataService;

    @Test
    public void testQueryClassifiedFormDataByTableId() {
        Map<Long, Map<String, Optional<TemplateDataBO>>> resultMap = templateFormDataService.queryClassifiedFormDataByTableId(328434438381764608L);
        System.out.println("JSONObject.toJSONString(resultMap) = " + JSONObject.toJSONString(resultMap));
    }
}
