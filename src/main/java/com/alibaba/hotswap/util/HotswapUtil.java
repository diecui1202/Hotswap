/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-5-23 17:18:41
 */
public class HotswapUtil {

    public static String getInternalClassName(String className) {
        return className.replace('.', '/');
    }

    public static void sysout(Object obj) {
        System.out.println("Hotswap[SYSOUT] - " + obj);
    }

    public static void sysoutRuntime(boolean stage) {
        sysout((stage == true ? "start:" : "end") + HotswapRuntime.CLASS_METAS);
    }
}
