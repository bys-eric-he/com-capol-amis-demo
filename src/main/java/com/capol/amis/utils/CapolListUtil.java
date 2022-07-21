package com.capol.amis.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/11 17:30
 */
public class CapolListUtil {

    /**
     * 获取 List&lt;List&lt;Map&lt;K, V&gt;&gt;&gt; 内层List中Map的笛卡尔积
     * @param listOfListMap ListMap的集合
     * @param <K> Map的key泛型
     * @param <V> Map的value泛型
     * @return 笛卡尔积后的结果
     * example: <br/>
     *          [{a: 1, b: 2}, {c: 3, d: 4}] 与 [{e: 5, f: 6}, {g: 7, h: 8} <br/>
     *          的笛卡尔积应该为
     *          [ <br/>
     *             &nbsp;&nbsp; {a: 1, b: 2, e: 5, f: 6}, <br/>
     *             &nbsp;&nbsp; {a: 1, b: 2, e: 7, f: 8}, <br/>
     *             &nbsp;&nbsp; {a: 3, b: 4, e: 5, f: 6}, <br/>
     *             &nbsp;&nbsp; {a: 3, b: 4, e: 7, f: 8}  <br/>
     *          ]
     */
    public static <K, V> List<Map<K, V>> buildCartesianListMap(List<List<Map<K, V>>> listOfListMap) {
        if (CollectionUtils.isEmpty(listOfListMap)) {
            return null;
        }
        List<Map<K, V>> hasPutMapList = new ArrayList<>();
        buildCartesianListMap(listOfListMap, hasPutMapList);
        return hasPutMapList;
    }

    private static <K, V> void buildCartesianListMap(List<List<Map<K, V>>> remaining,
                                                     List<Map<K, V>> hasPutMapList) {
        if (remaining.size() == 1) { // 递归终止
            addMap(remaining, hasPutMapList);
        } else {
            addMap(remaining, hasPutMapList);
            remaining.remove(0);
            buildCartesianListMap(remaining, hasPutMapList); // 递归添加
        }
    }

    private static <K, V> void addMap(List<List<Map<K, V>>> remaining,
                                      List<Map<K, V>> hasPutMapList) {
        List<Map<K, V>> toAdd = remaining.get(0);
        List<Map<K, V>> ans = new ArrayList<>();
        for (Map<K, V> add : toAdd) {
            if (hasPutMapList.size() == 0) {
                ans.add(add);
            } else {

                for (Map<K, V> hasPutMap : hasPutMapList) {
                    // 注意这里不要直接在hasPutMap上修改, list获取的是引用变量, 直接修改会对原有的数据造成影响
                    Map<K, V> map = new HashMap<>(hasPutMap);
                    map.putAll(add);
                    ans.add(map);
                }
            }
        }
        hasPutMapList.clear();
        hasPutMapList.addAll(ans);
    }
}
