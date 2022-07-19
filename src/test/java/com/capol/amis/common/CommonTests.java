package com.capol.amis.common;

import com.capol.amis.entity.bo.FormDataBasicVO;
import com.capol.amis.utils.CapolListUtil;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/8 14:05
 */
public class CommonTests {

    @Test
    public void testTrans() {
        FormDataBasicVO formData1 = new FormDataBasicVO().setRowId(1001L).setFieldName("field1").setFieldHash(1003L);
        FormDataBasicVO formData2 = new FormDataBasicVO().setRowId(1001L).setFieldName("field2").setFieldHash(2003L);
        FormDataBasicVO formData3 = new FormDataBasicVO().setRowId(1002L).setFieldName("field1").setFieldHash(3003L);
        FormDataBasicVO formData4 = new FormDataBasicVO().setRowId(1002L).setFieldName("field2").setFieldHash(4003L);
        FormDataBasicVO formData5 = new FormDataBasicVO().setRowId(1003L).setFieldName("field1").setFieldHash(5003L);
        FormDataBasicVO formData6 = new FormDataBasicVO().setRowId(1003L).setFieldName("field2").setFieldHash(6003L);

        List<FormDataBasicVO> collect = Stream.of(formData1, formData2, formData3, formData4, formData5, formData6).collect(Collectors.toList());

        Map<Long, List<FormDataBasicVO>> map = collect.stream().collect(Collectors.groupingBy(FormDataBasicVO::getRowId));
        Map<Long, Map<String, Long>> result = new HashMap<>();
        map.forEach((id, list) -> {
            Map<String, Long> dataMap = new HashMap<>();
            for (FormDataBasicVO data : list) {
                dataMap.put(data.getFieldName(), data.getFieldHash());
            }
            result.put(id, dataMap);
        });
        System.out.println(result);
    }

    @Test
    public void testTrans2() {
        FormDataBasicVO formData1 = new FormDataBasicVO().setRowId(1001L).setFieldName("field1").setFieldHash(1003L);
        FormDataBasicVO formData2 = new FormDataBasicVO().setRowId(1001L).setFieldName("field2").setFieldHash(2003L);
        FormDataBasicVO formData3 = new FormDataBasicVO().setRowId(1002L).setFieldName("field1").setFieldHash(3003L);
        FormDataBasicVO formData4 = new FormDataBasicVO().setRowId(1002L).setFieldName("field2").setFieldHash(4003L);
        FormDataBasicVO formData5 = new FormDataBasicVO().setRowId(1003L).setFieldName("field1").setFieldHash(5003L);
        FormDataBasicVO formData6 = new FormDataBasicVO().setRowId(1003L).setFieldName("field2").setFieldHash(6003L);

        List<FormDataBasicVO> collect = Stream.of(formData1, formData2, formData3, formData4, formData5, formData6).collect(Collectors.toList());

        Map<Long, Set<String>> collect1 = collect.stream()
                .collect(Collectors.groupingBy(FormDataBasicVO::getRowId, Collectors.mapping(FormDataBasicVO::getFieldName, Collectors.toSet())));

        System.out.println(collect1);
    }

    @Test
    public void testTrans3() {
        FormDataBasicVO formData1 = new FormDataBasicVO().setRowId(1001L).setFieldName("field1").setFieldHash(1003L);
        FormDataBasicVO formData2 = new FormDataBasicVO().setRowId(1001L).setFieldName("field2").setFieldHash(2003L);
        FormDataBasicVO formData3 = new FormDataBasicVO().setRowId(1002L).setFieldName("field1").setFieldHash(3003L);
        FormDataBasicVO formData4 = new FormDataBasicVO().setRowId(1002L).setFieldName("field2").setFieldHash(4003L);
        FormDataBasicVO formData5 = new FormDataBasicVO().setRowId(1003L).setFieldName("field1").setFieldHash(5003L);
        FormDataBasicVO formData6 = new FormDataBasicVO().setRowId(1003L).setFieldName("field2").setFieldHash(6003L);

        List<FormDataBasicVO> collect = Stream.of(formData1, formData2, formData3, formData4, formData5, formData6).collect(Collectors.toList());

        Map<Long, Map<Long, Set<String>>> collect1 = collect.stream()
                .collect(Collectors.groupingBy(FormDataBasicVO::getRowId,
                        Collectors.groupingBy(FormDataBasicVO::getFieldHash,
                                Collectors.mapping(FormDataBasicVO::getFieldName, Collectors.toSet()))));

        System.out.println(collect1);
    }

    @Test
    public void testReducing() {
        FormDataBasicVO formData1 = new FormDataBasicVO().setRowId(1001L).setFieldName("field1").setFieldHash(1003L);
        FormDataBasicVO formData2 = new FormDataBasicVO().setRowId(1001L).setFieldName("field2").setFieldHash(2003L);
        FormDataBasicVO formData3 = new FormDataBasicVO().setRowId(1002L).setFieldName("field1").setFieldHash(3003L);
        FormDataBasicVO formData4 = new FormDataBasicVO().setRowId(1002L).setFieldName("field2").setFieldHash(4003L);
        FormDataBasicVO formData5 = new FormDataBasicVO().setRowId(1003L).setFieldName("field1").setFieldHash(5003L);
        FormDataBasicVO formData6 = new FormDataBasicVO().setRowId(1003L).setFieldName("field2").setFieldHash(6003L);

        List<FormDataBasicVO> list = Stream.of(formData1, formData2, formData3, formData4, formData5, formData6).collect(Collectors.toList());

        Map<Long, Map<String, Optional<FormDataBasicVO>>> collect = list.stream().collect(
                Collectors.groupingBy(FormDataBasicVO::getRowId,
                        Collectors.groupingBy(FormDataBasicVO::getFieldName, Collectors.reducing((ldata, rdata) -> ldata))));

        System.out.println("collect = " + collect);

    }

    @Test
    public void testTreeMap() {
        Map<Long, String> map = new TreeMap<>();
        map.put(2L, "a");
        map.put(1L, "b");
        map.put(3L, "c");
        map.put(5L, "d");
        System.out.println(map);
    }

    @Test
    public void testPut() {
        Map<Long, Set<Long>> map = new TreeMap<>();
        Set<Long> ids = map.getOrDefault(1L, new HashSet<>());
        ids.add(13L);
        map.put(1L, ids);
        System.out.println("map = " + map);
    }

    @Test
    public void testMapList() {
        List<List<Map<String, Object>>> lists = new ArrayList<>();


        List<Map<String, Object>> listOfListMap1 = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("a", 1);
        map1.put("b", 2);
        Map< String, Object> map2 = new HashMap<>();
        map2.put("c", 3);
        map2.put("d", 4);
        Map< String, Object> map3 = new HashMap<>();
        map3.put("e", 5);
        map3.put("f", 6);
        Map< String, Object> map4 = new HashMap<>();
        map4.put("g", 7);
        map4.put("h", 8);

        listOfListMap1.add(map1);
        listOfListMap1.add(map2);


        List<Map<String, Object>> listOfListMap2 = new ArrayList<>();
        listOfListMap2.add(map3);
        listOfListMap2.add(map4);

        lists.add(listOfListMap1);
        lists.add(listOfListMap2);

        List<Map<String, Object>> rightMapList = CapolListUtil.buildCartesianListMap(lists);
        System.out.println("rightMapList = " + rightMapList);


        Map<String, Object> leftDataMap = new HashMap<>();
        leftDataMap.put("l1", 100);
        leftDataMap.put("l2", 200);

        for (Map<String, Object> rightMap : rightMapList) {
            Map<String, Object> map = new HashMap<>(leftDataMap);
            map.putAll(rightMap);
            System.out.println("leftDataMap = " + leftDataMap);
            System.out.println("map = " + map);
        }
    }

    @Test
    public void testMD5() {
        String pwd = "123456";
        String pwdMD5 = DigestUtils.md5DigestAsHex(pwd.getBytes());
        System.out.println(pwdMD5);
    }


}
