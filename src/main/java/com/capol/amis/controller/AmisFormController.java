package com.capol.amis.controller;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.annotation.RepeatSubmit;
import com.capol.amis.model.BusinessSubjectDataModel;
import com.capol.amis.model.BusinessSubjectFormModel;
import com.capol.amis.model.FormFieldConfigModel;
import com.capol.amis.response.CommonResult;
import com.capol.amis.response.ResultCode;
import com.capol.amis.service.IAmisFormConfigSevice;
import com.capol.amis.service.IAmisFormDataSevice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/amis/v1")
@Api(value = "/api/amis/v1", tags = "AMIS表单JSON数据解析存储服务")
@RestController
@Validated
@Slf4j
public class AmisFormController {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private IAmisFormConfigSevice iAmisFormConfigSevice;

    @Autowired
    private IAmisFormDataSevice iAmisFormDataSevice;

    /**
     * 保存AMIS表单JSON结构信息
     *
     * @param subjectFormModel
     * @return
     */
    @RepeatSubmit
    @ApiOperation("保存AMIS表单JSON结构信息")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @PostMapping("/saveBody")
    public CommonResult<String> saveBody(@RequestBody @Validated BusinessSubjectFormModel subjectFormModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return CommonResult.failed(ResultCode.PARAM_IS_INVALID);
        }
        log.info(JSONObject.toJSONString(subjectFormModel));
        String result = iAmisFormConfigSevice.saveFormFieldConfig(subjectFormModel);
        return CommonResult.success(result, "保存AMIS表单JSON结构信息数据成功, 处理服务端口：" + serverPort);
    }

    /**
     * 保存AMIS表单数据信息
     *
     * @param subjectDataModel
     * @return
     */
    @RepeatSubmit
    @ApiOperation("保存AMIS表单数据信息")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @PostMapping("/saveData")
    public CommonResult<String> saveData(@RequestBody @Validated BusinessSubjectDataModel subjectDataModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return CommonResult.failed(ResultCode.PARAM_IS_INVALID);
        }
        log.info(JSONObject.toJSONString(subjectDataModel));
        String result = iAmisFormDataSevice.insertData(subjectDataModel);
        return CommonResult.success(result, "保存AMIS表单数据成功, 处理服务端口：" + serverPort);
    }
}
