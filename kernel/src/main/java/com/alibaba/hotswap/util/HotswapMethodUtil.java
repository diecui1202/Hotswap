/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import com.alibaba.hotswap.constant.HotswapConstructorSign;
import com.alibaba.hotswap.runtime.HotswapMethodIndexHolder;

/**
 * @author yong.zhuy 2012-6-14
 */
public class HotswapMethodUtil {

    public static String getMethodKey(String name, String desc) {
        return name + "[" + desc + "]";
    }

    public static NoSuchMethodError noSuchMethodError(String className) {
        return new NoSuchMethodError(className.replace("/", ".") + ".<init>");
    }

    public static Object[] processConstructorArgs(Object[] objs, Object arg, int index) {
        objs[index] = arg;
        return objs;
    }

    public static Class<?>[] getConstructorParamTypes() {
        Class<?>[] types = new Class<?>[3];
        types[0] = HotswapConstructorSign.class;
        types[1] = int.class;
        types[2] = Object[].class;

        return types;
    }

    public static Object[] getMethodParams(String className, String name, String desc) {
        Object[] params = new Object[3];
        params[0] = null;
        params[1] = HotswapMethodIndexHolder.getMethodIndex(className, name, desc);
        params[2] = null;

        return params;
    }
}
