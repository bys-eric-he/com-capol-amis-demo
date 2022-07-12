package com.capol.amis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.TemplateFormConfDO;
import com.capol.amis.enums.ComponentFieldEnum;
import com.capol.amis.enums.ComponentTypeEnum;
import com.capol.amis.model.param.FormFieldConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class AmisUtil {

    public static final String BODY = "body";
    public static final String TYPE = "type";

    public static final String FORM = "form";
    public static final String PAGE = "page";

    public static final String TITLE ="title";

    public static final String TABS = "tabs";
    public static final String GRID = "grid";

    public static final String COLUMNS = "columns";
    public static final String NAME = "name";
    public static final String LABEL = "label";

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
     * @param formTable
     */
    public static void parseFormBody(List<JSONObject> formFields, Map<String, List<JSONObject>> gridFields, JSONObject formTable) {
        JSONArray formBody = null;
        if (formTable != null) {
            formBody = (JSONArray) formTable.get(BODY);
        }

        if (CollectionUtils.isEmpty(formBody)) {
            //表单内容为空，不继续解析创建表
            log.warn("--------------表单内容为空，不继续解析创建表------------------");
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
                parseInputTable(gridFields, jsonObject);
            } else if (CONTAINER_TYPE.contains(type)) {
                //嵌套容器：三栏、两栏、选项卡
                parseContainer(formFields, jsonObject, type);
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

    /**
     * 解析表格编辑框组件
     *
     * @param gridFields
     * @param gridTable
     */
    private static void parseInputTable(Map<String, List<JSONObject>> gridFields, JSONObject gridTable) {
        //取出列名
        JSONArray cloumns = (JSONArray) gridTable.get(COLUMNS);
        //取出表名
        String tableName = gridTable.getString(NAME);
        List<JSONObject> gridJsonObjects = new ArrayList<>();
        //遍历表格中的字段，目前只支持text类型
        for (Object cloumn : cloumns) {
            JSONObject jsonObject = (JSONObject) cloumn;
            String type = jsonObject.getString(TYPE);
            String columnName = jsonObject.getString(NAME);
            if (StringUtils.isBlank(columnName)) {
                log.error("解析表格编辑框组件错误, 表格成生的自动标识不能为空!");
                return;
            }
            if (FORM_TYPE.contains(type)) {
                gridJsonObjects.add(jsonObject);
            }
        }

        gridFields.put(tableName, gridJsonObjects);
    }

    /**
     * 解析容器类型组件，如三栏、两栏、选项卡
     * 只支持解析两层
     *
     * @param formFields
     * @param formTable
     * @param type
     */
    private static void parseContainer(List<JSONObject> formFields, JSONObject formTable, String type) {
        //容器组件嵌套层数，超过两层就不再处理
        int count = 1;
        log.info("---解析容器类型组件，如三栏、两栏、选项卡,只支持解析两层---");
        //选项卡
        if (TABS.equals(type)) {
            JSONArray tabs = (JSONArray) formTable.get(TABS);
            if (CollectionUtils.isNotEmpty(tabs)) {
                //选项卡是多个的情况
                for (Object tab : tabs) {
                    JSONObject jsonTab = (JSONObject) tab;
                    //每个分栏下是body
                    if (!(jsonTab.get(BODY) instanceof JSONArray)) {
                        continue;
                    }
                    JSONArray body = (JSONArray) jsonTab.get(BODY);
                    parseBody(formFields, body, count);
                }
            }
        }

        //分栏
        if (GRID.equals(type)) {
            JSONArray columns = (JSONArray) formTable.get(COLUMNS);
            if (CollectionUtils.isNotEmpty(columns)) {
                //分栏是多个
                for (Object column : columns) {
                    JSONObject jsonColumn = (JSONObject) column;
                    //每个分栏下是Body
                    JSONArray body = (JSONArray) jsonColumn.get(BODY);
                    parseBody(formFields, body, count);
                }
            }
        }
    }

    /**
     * 解析body
     *
     * @param formFields
     * @param body
     * @param count
     */
    private static void parseBody(List<JSONObject> formFields, JSONArray body, int count) {
        for (Object object : body) {
            if (!(object instanceof JSONObject)) {
                continue;
            }
            log.info("--解析容器类型组件body--");
            JSONObject jsonObject = (JSONObject) object;
            String type = jsonObject.getString(TYPE);
            //表单项组件
            if (FORM_TYPE.contains(type)) {
                formFields.add(jsonObject);
            } else if (CONTAINER_TYPE.contains(type)) {
                //三栏、两栏、选项卡
                count++;
                if (count > 2) {
                    //嵌套大于两层不处理
                    continue;
                }
                if (TABS.equals(type)) {
                    parseTabs(formFields, jsonObject, count);
                }
                if (GRID.equals(type)) {
                    parseGrid(formFields, jsonObject, count);
                }
            }
        }
    }

    /**
     * 解析选项卡
     *
     * @param formFields
     * @param jsonObject
     * @param count
     */
    private static void parseTabs(List<JSONObject> formFields, JSONObject jsonObject, int count) {
        JSONArray tabs = (JSONArray) jsonObject.get(TABS);
        if (CollectionUtils.isNotEmpty(tabs)) {
            log.info("--解析选项卡--");
            //多个选项卡的情况
            for (Object tab : tabs) {
                JSONObject jsonTab = (JSONObject) tab;
                // 每个分栏下是body
                if (!(jsonTab.get(BODY) instanceof JSONArray)) {
                    continue;
                }

                JSONArray body = (JSONArray) jsonTab.get(BODY);
                parseBody(formFields, body, count);
            }
        }
    }

    /**
     * 解析分栏
     *
     * @param formFields
     * @param jsonObject
     * @param count
     */
    private static void parseGrid(List<JSONObject> formFields, JSONObject jsonObject, int count) {
        JSONArray columns = (JSONArray) jsonObject.get(COLUMNS);
        if (CollectionUtils.isNotEmpty(columns)) {
            log.info("--解析分栏--");
            //分栏是多个
            for (Object column : columns) {
                JSONObject jsonColumn = (JSONObject) column;

                //每个分栏下是body
                JSONArray body = (JSONArray) jsonColumn.get(BODY);
                parseBody(formFields, body, count);
            }
        }
    }
}
