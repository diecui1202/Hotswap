/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yong.zhuy 2012-6-14
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HotswapFieldUtil {

    public static String getFieldKey(String name, String desc) {
        return name + "(" + desc + ")";
    }

    public static void setFieldValue(Object value, ConcurrentHashMap fieldHolder, String name, String desc) {
        fieldHolder.put(getFieldKey(name, desc), value);
    }

    public static Object getFieldValue(ConcurrentHashMap fieldHolder, String name, String desc) {
        return fieldHolder.get(getFieldKey(name, desc));
    }
}
