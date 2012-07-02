/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.MethodMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-6-14
 */
public class HotswapMethodUtil {

    public static String getMethodKey(String name, String desc) {
        return name + "[" + desc + "]";
    }

    public static NoSuchMethodError noSuchMethodError(String className, int index) {
        ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
        MethodMeta methodMeta = classMeta.methodMetas.get(index);
        return new NoSuchMethodError(className.replace("/", ".") + "." + methodMeta.desc);
    }
}
