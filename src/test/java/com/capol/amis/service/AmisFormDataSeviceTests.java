package com.capol.amis.service;

import com.capol.amis.AmisApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 11:08
 * desc: Amis表单服务查询
 */
@Slf4j
public class AmisFormDataSeviceTests extends AmisApplicationTests {
    @Autowired
    private IAmisFormDataSevice amisFormDataSevice;
    
    @Test
    public void testGetDetail() {
        Map<String, Object> detail = amisFormDataSevice.getDetail(1745662207332353L, 326295511084564480L);
        detail.forEach((key, value) -> log.info("{} >++++++++++++++++++++< {}", key, value));
    }


}
