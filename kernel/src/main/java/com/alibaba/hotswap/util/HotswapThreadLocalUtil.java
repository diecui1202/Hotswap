/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.hotswap.meta.ClassMeta;

/**
 * @author zhuyong 2012-6-26
 */
public class HotswapThreadLocalUtil {

    private static final ThreadLocal<String>                 classNameThreadLocal    = new ThreadLocal<String>();

    private static final ThreadLocal<Map<String, ClassMeta>> currentReloadingClasses = new ThreadLocal<Map<String, ClassMeta>>();

    static {
        currentReloadingClasses.set(new HashMap<String, ClassMeta>());
    }

    public static void setClassName(String className) {
        classNameThreadLocal.set(className);
    }

    public static String getClassName() {
        return classNameThreadLocal.get();
    }

    public static boolean isReloading(String className) {
        Map<String, ClassMeta> reloadings = currentReloadingClasses.get();
        if (reloadings.containsKey(className)) {
            return true;
        }

        return false;
    }

    public static void addReloading(String className, ClassMeta classMeta) {
        Map<String, ClassMeta> reloadings = currentReloadingClasses.get();
        reloadings.put(className, classMeta);
    }

    public static void removeReloading(String className) {
        Map<String, ClassMeta> reloadings = currentReloadingClasses.get();
        reloadings.remove(className);
    }
}
