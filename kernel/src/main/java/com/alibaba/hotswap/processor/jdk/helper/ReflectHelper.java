/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.helper;

import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author zhuyong 2012-7-17
 */
public class ReflectHelper {

    public static Class<?> getVClassByClass(Class<?> clazz) {
        String className = clazz.getName();
        if (!HotswapRuntime.hasClassMeta(className)) {
            return null;
        }
        return HotswapRuntime.getClassMeta(className).vClass;
    }
}
