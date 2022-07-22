package com.capol.amis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.capol.amis.enums.ComponentFieldEnum;
import com.capol.amis.enums.ComponentTypeEnum;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class AmisUtil {

    /**
     * 表单JSON对象中的body属性key
     */
    public static final String BODY = "body";
    /**
     * 表单JSON对象中的组件类型属性key
     */
    public static final String TYPE = "type";
    /**
     * 表单JSON对象中的表单属性key
     */
    public static final String FORM = "form";
    /**
     * 表单JSON对象中的body属性key
     */
    public static final String PAGE = "page";
    /**
     * 表单JSON对象中的标题属性key
     */
    public static final String TITLE = "title";
    /**
     * 表单JSON对象中的选项属性key
     */
    public static final String OPTIONS = "options";
    /**
     * 表单JSON对象中的选项卡属性key
     */
    public static final String TABS = "tabs";
    /**
     * 表单JSON对象中的分栏属性key
     */
    public static final String GRID = "grid";
    /**
     * 表单JSON对象中的列数组属性key
     */
    public static final String COLUMNS = "columns";
    /**
     * 表单JSON对象中的标识属性key
     */
    public static final String NAME = "name";
    /**
     * 表单JSON对象中的标签属性key
     */
    public static final String LABEL = "label";
    /**
     * 表单JSON对象中的值属性key
     */
    public static final String VALUE = "value";

    public static final String PICKER = "picker";
    public static final String TREE_SELECT = "tree-select";

    /**
     * 表单JSON对象中的验证对象属性key
     */
    public static final String VALIDATIONS = "validations";
    /**
     * 表单JSON对象中的最大长度属性key
     */
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
     * 获取下拉选项options数组
     *
     * @param jsonObject
     * @return
     */
    public static JSONArray getSelectOptions(JSONObject jsonObject) {
        JSONArray jsonArray = (JSONArray) jsonObject.get(OPTIONS);
        return jsonArray;
    }

    /**
     * @param value
     * @param componentType
     * @param fieldKey
     * @param configJSon
     * @return
     */
    public static String getTransferValue(String value, String componentType, String fieldKey, String configJSon) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        if (StringUtils.isBlank(componentType)) {
            return value;
        }
        String line = "-";
        String transferValue = value;

        // 日期框
        if (componentType.equals(ComponentFieldEnum.INPUT_DATE.getValue())) {
            transferValue = getDateByPattern(value, DateUtil.DEFAULT_DATE_FORMAT);
        }
        //Month框
        if (componentType.equals(ComponentFieldEnum.INPUT_MONTH.getValue())) {
            transferValue = getDateByPattern(value, DateUtil.DEFAULT_DATE_YYYY_MM_FORMAT);
        }
        //Quarter框
        if (componentType.equals(ComponentFieldEnum.INPUT_QUARTER.getValue())) {
            transferValue = getQuarter(value);
        }
        //Year框
        if (componentType.equals(ComponentFieldEnum.INPUT_YEAR.getValue())) {
            Object formatObj = getObjectByJson(configJSon, fieldKey, "format");
            String format = "X";
            if (formatObj != null) {
                format = formatObj.toString();
            }
            transferValue = getDateByPatternYYYY(format, value, DateUtil.DEFAULT_DATE_YYYY_FORMAT);
        }
        //日期范围
        if (componentType.equals(ComponentFieldEnum.INPUT_DATE_RANGE.getValue())) {
            String[] values = value.split(",");
            transferValue = getDateByPattern(values[0], DateUtil.DEFAULT_DATE_FORMAT) + line + getDateByPattern(values[1], DateUtil.DEFAULT_DATE_FORMAT);
        }
        //日期时间范围
        if (componentType.equals(ComponentFieldEnum.INPUT_DATETIME_RANGE.getValue())) {
            String[] values = value.split(",");
            transferValue = getDateByPattern(values[0], DateUtil.DEFAULT_DATE_TIME_FORMAT + line + getDateByPattern(values[1], DateUtil.DEFAULT_DATE_TIME_FORMAT));
        }
        //月份范围
        if (componentType.equals(ComponentFieldEnum.INPUT_MONTH_RANGE.getValue())) {
            String[] values = value.split(",");
            transferValue = getDateByPattern(values[0], DateUtil.DEFAULT_DATE_YYYY_MM_FORMAT) + line + getDateByPattern(values[1], DateUtil.DEFAULT_DATE_YYYY_MM_FORMAT);
        }
        //日期时间
        if (componentType.equals(ComponentFieldEnum.INPUT_DATETIME.getValue())) {
            transferValue = getDateByPattern(value, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        }

        return transferValue;
    }

    /**
     * 通过关联组件标识(pickerStaff) 和 json 查询需要回显的字段名
     *
     * @param configJson
     * @param componentType
     * @param fieldName
     * @return
     */
    public static Object getObjectByJson(String configJson, String componentType, String fieldName) {
        if (StringUtil.isBlank(configJson)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(configJson);
        JSONArray formBody = jsonObject.getJSONArray(BODY);
        if (CollectionUtils.isNotEmpty(formBody)) {
            return null;
        }

        //开始循环formBody的数组,解析组件
        for (int i = 0; i < formBody.size(); i++) {
            JSONObject jsonObj = formBody.getJSONObject(i);
            JSONArray body = jsonObj.getJSONArray(BODY);
            if (CollectionUtils.isEmpty(body)) {
                continue;
            }

            for (int j = 0; j < body.size(); j++) {
                JSONObject jsonObjChild = body.getJSONObject(j);
                JSONArray columns = jsonObjChild.getJSONArray(COLUMNS);
                if (columns == null || columns.size() == 0) {
                    String name = jsonObjChild.getString(NAME);
                    if (StringUtils.isBlank(name)) {
                        return null;
                    }
                    if (componentType.equals(name)) {
                        return jsonObjChild.get(fieldName);
                    }
                } else {
                    for (int k = 0; k < columns.size(); k++) {
                        JSONObject jsonObjectColumn = columns.getJSONObject(k);
                        JSONArray jsonArrayColumn = jsonObjectColumn.getJSONArray(BODY);
                        if (jsonArrayColumn != null) {
                            for (int t = 0; t < jsonArrayColumn.size(); t++) {
                                JSONObject jsonObject4SanLan = jsonArrayColumn.getJSONObject(t);
                                if (jsonObject4SanLan != null) {
                                    String name = jsonObject4SanLan.getString(NAME);
                                    if (StringUtils.isBlank(name)) {
                                        return null;
                                    }
                                    if (componentType.equals(name)) {
                                        return jsonObject4SanLan.get(fieldName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 格式化时间-年
     *
     * @param format
     * @param value
     * @param pattern
     * @return
     */
    private static String getDateByPatternYYYY(String format, String value, String pattern) {
        Date date = new Date();
        //x:毫秒级 X:秒级
        if (format.equals("x")) {
            date.setTime(Long.valueOf(value));
        } else {
            date.setTime(Long.valueOf(value) * 1000);
        }
        return DateUtil.formatDate(date, pattern);
    }

    /**
     * 格式化日期
     *
     * @param value
     * @param pattern
     * @return
     */
    private static String getDateByPattern(String value, String pattern) {
        String formatDate = null;
        Date date = new Date();
        if (value.length() == 10) {
            value = value + "000";
        }

        date.setTime(Long.valueOf(value));
        if (pattern != null && pattern.length() > 0) {
            formatDate = DateFormatUtils.format(date, pattern);
        } else {
            formatDate = DateFormatUtils.format(date, DateUtil.DEFAULT_DATE_FORMAT);
        }
        return formatDate;
    }

    /**
     * 一刻钟(15分钟)
     *
     * @param value
     * @return
     */
    private static String getQuarter(String value) {
        String transferValue;
        Date date = new Date();
        if (value.length() == 10) {
            value = value + "000";
        }
        date.setTime(Long.valueOf(value));
        transferValue = cn.hutool.core.date.DateUtil.yearAndQuarter(date);
        transferValue = transferValue.substring(0, 4) + "-Q" + transferValue.substring(4);
        return transferValue;
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
                    log.info("---------容器嵌套大于两层, 不处理!!!---------");
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
