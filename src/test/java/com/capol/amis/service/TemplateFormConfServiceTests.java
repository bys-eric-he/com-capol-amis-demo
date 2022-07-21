package com.capol.amis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.capol.amis.entity.TemplateFormConfDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 18:47
 * desc:
 */
public class TemplateFormConfServiceTests extends AmisFormDataSeviceTests {

    @Autowired
    private IFormFieldConfigService formFieldConfigService;

    @Autowired
    private ITemplateFormConfService templateFormConfService;

    @Test
    public void testFormField() {
        QueryWrapper<TemplateFormConfDO> wrapper = new QueryWrapper<>();
        wrapper.select("distinct table_id");
        List<Long> tableIds = templateFormConfService.listObjs(wrapper, o -> (Long) o);
        System.out.println("tableIds = " + tableIds);
    }

}
