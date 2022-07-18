package com.capol.amis.controller;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.annotation.RepeatSubmit;
import com.capol.amis.model.param.BusinessSubjectDataModel;
import com.capol.amis.model.result.FormDataInfoModel;
import com.capol.amis.response.CommonResult;
import com.capol.amis.response.ResultCode;
import com.capol.amis.service.IAmisFormDataSevice;
import com.capol.amis.vo.DynamicDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/amis/data")
@Api(value = "/api/amis/data", tags = "AMIS表单数据查询服务")
@RestController
@Validated
@Slf4j
public class AmisDataController {
    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private IAmisFormDataSevice iAmisFormDataSevice;

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

    /**
     * 更新AMIS表单数据信息
     *
     * @param subjectDataModel
     * @return
     */
    @RepeatSubmit
    @ApiOperation("更新AMIS表单数据信息")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @PostMapping("/updateData")
    public CommonResult<String> updateData(@RequestBody @Validated BusinessSubjectDataModel subjectDataModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return CommonResult.failed(ResultCode.PARAM_IS_INVALID);
        }
        log.info(JSONObject.toJSONString(subjectDataModel));
        String result = iAmisFormDataSevice.updateData(subjectDataModel);
        return CommonResult.success(result, "更新AMIS表单数据信息成功, 处理服务端口：" + serverPort);
    }

    /**
     * 删除AMIS表单数据信息
     *
     * @param subjectId
     * @param rowId
     * @return
     */
    @RepeatSubmit
    @ApiOperation("删除AMIS表单数据信息")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @DeleteMapping("/deleteData/{subjectId}/{rowId}")
    public CommonResult<String> deleteData(@RequestParam Long subjectId, @RequestParam Long rowId) {
        String result = iAmisFormDataSevice.deleteData(subjectId, rowId);
        return CommonResult.success(result, "删除AMIS表单数据信息成功, 处理服务端口：" + serverPort);
    }

    /**
     * 查询表单数据(主表+从表,行转列)
     *
     * @param subjectId
     * @return
     */
    @ApiOperation("查询表单数据(主表+从表,行转列)")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @GetMapping("/queryDataList")
    public CommonResult<List<FormDataInfoModel>> queryFormDataList(Long subjectId) {
        List<FormDataInfoModel> result = iAmisFormDataSevice.queryFormDataList(subjectId);
        return CommonResult.success(result, "查询AMIS表单数据(主表+从表)成功, 处理服务端口：" + serverPort);
    }

    /**
     * 查询表单数据(仅主表数据)
     *
     * @param subjectId
     * @return
     */
    @ApiOperation("查询表单数据(行转列-仅主表数据)")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @GetMapping("/queryDataMaps")
    public CommonResult<DynamicDataVO> queryFormDataMaps(Long subjectId) {
        DynamicDataVO result = iAmisFormDataSevice.queryFormDataMaps(subjectId);
        return CommonResult.success(result, "查询表单数据(行转列-仅主表数据)成功, 处理服务端口：" + serverPort);
    }

    /**
     * 查询指定表单数据明细(包括从表数据)
     *
     * @param subjectId
     * @param rowId
     * @return
     */
    @ApiOperation("查询指定表单数据明细(包括从表数据)")
    @ApiResponses({@ApiResponse(code = 400, message = "请求失败!"), @ApiResponse(code = 200, message = "请求成功!"),})
    @GetMapping("/getDetail")
    public CommonResult<Map<String, Object>> getDetail(Long subjectId, Long rowId) {
        Map<String, Object> result = iAmisFormDataSevice.getDetail(subjectId, rowId);
        return CommonResult.success(result, "查询指定表单数据明细(包括从表数据)成功, 处理服务端口：" + serverPort);
    }
}
