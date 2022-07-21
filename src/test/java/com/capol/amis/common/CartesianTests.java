package com.capol.amis.common;

import org.apache.commons.collections4.CollectionUtils;

import java.io.*;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/11 15:08
 * desc:
 */
public class CartesianTests {

    public static void main(String[] args) {



    }



    public static void test3() {
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

        List<Map<String, Object>> maps = buildCartesianListMap(lists);

        maps.forEach(System.out::println);
    }


    public static void test2() {
        List<List<Integer>> origin = new ArrayList<>();
        List<Integer> list1 = Stream.of(1, 2, 3).collect(Collectors.toList());
        List<Integer> list2 = Stream.of(4, 5, 6).collect(Collectors.toList());
        List<Integer> list3 = Stream.of(7, 8, 9).collect(Collectors.toList());

        origin.add(list1);
        origin.add(list2);
        origin.add(list3);

        List<List<Integer>> result = result(origin);

        for (List<Integer> integers : result) {
            System.out.println(integers);
        }
    }

    public static void test1() {
        int[] a = {1, 2, 3, 4};
        permute(a);
        for (List<Integer> b : permute(a)) {
            System.out.println(b);
        }
    }

    public static List<Map<String, Object>> buildCartesianListMap(List<List<Map<String, Object>>> remaining) {
        if (CollectionUtils.isEmpty(remaining)) {
            return null;
        }
        List<Map<String, Object>> hasPutMapList = new ArrayList<>();
        buildCartesianListMap(remaining, hasPutMapList);
        return hasPutMapList;
    }

    public static void buildCartesianListMap(List<List<Map<String, Object>>> remaining,
                                             List<Map<String, Object>> hasPutMapList) {
        if (remaining.size() == 1) {
            addMap(remaining, hasPutMapList);
        } else {
            addMap(remaining, hasPutMapList);
            remaining.remove(0);
            buildCartesianListMap(remaining, hasPutMapList);
        }
    }

    private static void addMap(List<List<Map<String, Object>>> remaining,
                               List<Map<String, Object>> hasPutMapList) {
        List<Map<String, Object>> toAdd = remaining.get(0);
        List<Map<String, Object>> ans = new ArrayList<>();
        for (Map<String, Object> add : toAdd) {
            if (hasPutMapList.size() == 0) {
                ans.add(add);
            } else {

                for (Map<String, Object> hasPutMap : hasPutMapList) {
                    // 注意这里不要直接在hasPutMap上修改, list获取的是引用变量, 直接修改会对原有的数据造成影响
                    Map<String, Object> map = new HashMap<>(hasPutMap);

                    map.putAll(add);
                    ans.add(map);
                }
            }
        }
        hasPutMapList.clear();
        hasPutMapList.addAll(ans);
    }



    public static List<List<Integer>> result(List<List<Integer>> remaining) {
        if (CollectionUtils.isEmpty(remaining)) {
            return remaining;
        }
        List<List<Integer>> ans = new ArrayList<>();
        hehe2(remaining, ans);
        return ans;
    }

    public static void hehe2(List<List<Integer>> remaining,
                             List<List<Integer>> all) {
        if (remaining.size() == 1) {
            add(remaining, all);
        } else {
            add(remaining, all);
            remaining.remove(0);
            hehe2(remaining, all);
        }
    }

    private static void add(List<List<Integer>> remaining, List<List<Integer>> all) {
        List<Integer> toAdd = remaining.get(0);
        List<List<Integer>> ans = new ArrayList<>();
        for (Integer add : toAdd) {
            // 第一次添加
            if (all.size() == 0) {
                List<Integer> init = new ArrayList<>();
                init.add(add);
                ans.add(init);
            } else {
                for (List<Integer> integers : all) {
                    // 注意这里需要使用深拷贝
                    List<Integer> li = deepCopy(integers);
                    li.add(add);
                    ans.add(li);
                }
            }
        }
        all.clear();
        all.addAll(ans);
    }



    @SuppressWarnings("unchecked")
    public static <T> List<T> deepCopy(List<T> src) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        List<T> dest = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }


    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> all = new ArrayList<>();
        allSort(nums, 0, nums.length - 1, all);
        return all;
    }

    public static void allSort(int[] array, int begin, int end, List<List<Integer>> all) {
        if (begin == end) {
            List<Integer> origi = new ArrayList<>();
            for (int a : array) {
                origi.add(a);
            }
            all.add(origi);
            return;
        }

        for (int i = begin; i <= end; i++) {
            swap(array, begin, i);
            allSort(array, begin + 1, end, all);
            swap(array, begin, i);
        }

    }

    public static void swap(int[] array, int a, int b) {
        int tem = array[a];
        array[a] = array[b];
        array[b] = tem;
    }

}
