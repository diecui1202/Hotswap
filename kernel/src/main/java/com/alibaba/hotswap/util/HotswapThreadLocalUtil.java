/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

/**
 * @author zhuyong 2012-6-26
 */
public class HotswapThreadLocalUtil {

    private static final ThreadLocal<String> classNameThreadLocal = new ThreadLocal<String>();

    public static void setClassName(String className) {
        classNameThreadLocal.set(className);
    }

    public static String getClassName() {
        return classNameThreadLocal.get();
    }
}
