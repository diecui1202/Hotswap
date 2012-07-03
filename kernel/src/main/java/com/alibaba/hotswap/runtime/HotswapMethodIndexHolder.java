/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.runtime;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.hotswap.util.HotswapMethodUtil;
import com.alibaba.hotswap.util.HotswapUtil;

/**
 * @author zhuyong 2012-7-3
 */
public class HotswapMethodIndexHolder {

    private static HashMap<String, Map<String, Integer>> METHOD_INDEX_HOLDER = new HashMap<String, Map<String, Integer>>();

    public static int getMethodIndex(String className, String name, String desc) {
        className = HotswapUtil.getInternalClassName(className);

        Map<String, Integer> indexs = METHOD_INDEX_HOLDER.get(className);
        if (indexs == null) {
            indexs = new HashMap<String, Integer>();
            METHOD_INDEX_HOLDER.put(className, indexs);
        }

        String key = HotswapMethodUtil.getMethodKey(name, desc);
        Integer index = indexs.get(key);
        if (index == null) {
            index = indexs.size();
            indexs.put(key, index);
        }

        return index;
    }
}
