package com.capol.amis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.capol.amis.enums.ComponentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class AmisUtil {

    public static final String BODY = "body";
    public static final String TYPE = "type";

    public static final String FORM = "form";
    public static final String PAGE = "page";

    public static final String PICKER = "picker";
    public static final String TREE_SELECT = "tree-select";

    public static final String VALIDATIONS = "validations";
    public static final String MAXLENGTH = "maxLength";

    /**
     * 表单项组件
     */
    public static final List<String> FORM_TYPE = new ArrayList<>();

    /**
     * 嵌套容器组件：三栏、两栏、选项卡
     */
    public static final List<String> CONTAINER_TYPE = new ArrayList<>();

    /**
     * 将组件放入对应的类别中
     */
    static {
        ComponentTypeEnum[] values = ComponentTypeEnum.values();
        for (ComponentTypeEnum value : values) {
            switch (value.getType()) {
                case 1:
                    FORM_TYPE.add(value.getValue());
                    break;
                case 3:
                    CONTAINER_TYPE.add(value.getValue());
                    break;
            }
        }
    }

    /**
     * 解析得到表单对象
     *
     * @param json
     * @return
     */
    public static JSONObject getFormObject(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray body = (JSONArray) jsonObject.get(BODY);

        JSONObject form = null;

        for (Object object : body) {
            JSONObject jsonObj = (JSONObject) object;
            String type = jsonObj.getString(TYPE);
            if (FORM.equals(type)) {
                form = (JSONObject) object;
            }
        }

        return form;
    }

    /**
     * 解析表单
     *
     * @param formFields
     * @param gridFields
     * @param gridTableMap
     * @param formTable
     */
    public static void parseFormBody(List<JSONObject> formFields,
                                     Map<String, List<JSONObject>> gridFields,
                                     Map<String, JSONObject> gridTableMap,
                                     JSONObject formTable) {

        JSONArray formBody = null;
        if (formTable != null) {
            formBody = (JSONArray) formTable.get(BODY);
        }

        if (CollectionUtils.isEmpty(formBody)) {
            //表单内容为空，不继续解析创建表
            return;
        }

        // 开始循环form body的数组，解析组件
        for (Object object : formBody) {
            JSONObject jsonObject = (JSONObject) object;
            String type = jsonObject.getString(TYPE);
            //解析组件分支
            if (FORM_TYPE.contains(type)) {
                //表单项组件
                formFields.add(jsonObject);
            } else if (ComponentTypeEnum.INPUT_TABLE.getValue().equals(type)) {
                //表格编辑框
            } else if (CONTAINER_TYPE.contains(type)) {
                //嵌套容器：三栏、两栏、选项卡
            } else {
                if (PAGE.equalsIgnoreCase(type)) {
                    JSONArray body = (JSONArray) jsonObject.get(BODY);
                    if (CollectionUtils.isEmpty(body)) {
                        continue;
                    }
                    for (Object obj : body) {
                        JSONObject jsonObjectChild = (JSONObject) obj;
                        String typeChild = jsonObjectChild.getString(TYPE);
                        if (PICKER.equals(typeChild)) {
                            formFields.add(jsonObjectChild);
                        }
                        if (TREE_SELECT.equals(typeChild)) {
                            formFields.add(jsonObjectChild);
                        }
                    }
                } else {
                    //其它场景不解析
                    continue;
                }
            }
        }
    }

    /**
     * 获取验证最大长度
     *
     * @param jsonObject
     * @return
     */
    public static Integer getMaxLength(JSONObject jsonObject) {
        JSONObject validations = (JSONObject) jsonObject.get(VALIDATIONS);
        if (validations != null && validations.get(MAXLENGTH) != null) {
            return (Integer) validations.get(MAXLENGTH);
        }
        return null;
    }
}
