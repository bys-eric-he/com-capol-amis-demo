package com.capol.amis.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 行转列工具类
 */
@Slf4j
public class RowConvertColUtil {
    private static final String NULL_VALUE = "";
    private static final String HEADER_NULL_VALUE = "";
    private static Set<Object> headerSet;
    private static Set<Object> firstColSet;

    private RowConvertColUtil() {
    }

    public static class ConvertData {
        private Set<Object> headerSet;
        private Set<Object> firstColSet;
        private List<List<Object>> dataList;

        public ConvertData(List<List<Object>> dataList, Set<Object> headerSet, Set<Object> firstColSet) {
            this.headerSet = headerSet;
            this.firstColSet = firstColSet;
            this.dataList = dataList;
        }

        public Set<Object> getHeaderSet() {
            return headerSet;
        }

        public void setHeaderSet(Set<Object> headerSet) {
            this.headerSet = headerSet;
        }

        public Set<Object> getFirstColSet() {
            return firstColSet;
        }

        public void setFirstColSet(Set<Object> firstColSet) {
            this.firstColSet = firstColSet;
        }

        public List<List<Object>> getDataList() {
            return dataList;
        }

        public void setDataList(List<List<Object>> dataList) {
            this.dataList = dataList;
        }
    }

    /**
     * 行转列,返回ConvertData
     *
     * @param orignalList   原始list
     * @param headerName    列表头字段名
     * @param firstColName  首列字段名
     * @param valueFiedName 值列的字段名
     * @param needHeader    是否需要返回列表头
     * @return ConvertData
     */
    public static synchronized ConvertData doConvertReturnObj(List orignalList, String headerName, String firstColName, String valueFiedName, boolean needHeader) throws Exception {
        List<List<Object>> lists = doConvert(orignalList, headerName, firstColName, valueFiedName, needHeader);
        return new ConvertData(lists, headerSet, firstColSet);
    }

    /**
     * 行转列,返回转换后的list
     *
     * @param orignalList   原始list
     * @param headerName    列表头字段名
     * @param firstColName  首列字段名
     * @param valueFiedName 值列的字段名
     * @param needHeader    是否需要返回列表头
     */
    public static synchronized List<List<Object>> doConvert(List orignalList, String headerName, String firstColName, String valueFiedName, boolean needHeader) throws Exception {
        headerSet = new LinkedHashSet<>();
        firstColSet = new LinkedHashSet<>();
        List<List<Object>> resultList = new ArrayList<>();

        getHeaderFirstColSet(orignalList, headerName, firstColName);
        if (needHeader) {
            List<Object> headerList = new ArrayList<>();
            //填充进header
            headerList.add(HEADER_NULL_VALUE);
            headerList.addAll(headerSet);
            resultList.add(headerList);
        }
        for (Object firstColNameItem : firstColSet) {
            List<Object> colList = new ArrayList<>();
            //名称
            colList.add(firstColNameItem);
            for (Object headerItem : headerSet) {
                boolean flag = true;
                for (Object orignalObjectItem : orignalList) {
                    Field headerField = orignalObjectItem.getClass().getDeclaredField(headerName);
                    headerField.setAccessible(true);
                    Field firstColField = orignalObjectItem.getClass().getDeclaredField(firstColName);
                    firstColField.setAccessible(true);
                    Field valueField = orignalObjectItem.getClass().getDeclaredField(valueFiedName);
                    valueField.setAccessible(true);
                    if (headerItem.equals(headerField.get(orignalObjectItem))) {
                        if (firstColNameItem.equals(firstColField.get(orignalObjectItem))) {
                            colList.add(valueField.get(orignalObjectItem));
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    colList.add(NULL_VALUE);
                }
            }
            resultList.add(colList);
        }
        return resultList;
    }

    private static void getHeaderFirstColSet(List orignalList, String headerName, String firstColName) {
        try {
            for (Object item : orignalList) {
                Field headerField = item.getClass().getDeclaredField(headerName);
                headerField.setAccessible(true);
                Field firstColField = item.getClass().getDeclaredField(firstColName);
                firstColField.setAccessible(true);
                headerSet.add(headerField.get(item));
                firstColSet.add(firstColField.get(item));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
