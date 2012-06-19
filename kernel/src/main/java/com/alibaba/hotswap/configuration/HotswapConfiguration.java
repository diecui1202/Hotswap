/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yong.zhuy 2012-6-13
 */
public class HotswapConfiguration {

    public static boolean      VERBOSE        = false;
    public static String       TRACE          = null;

    public static List<String> WORKSPACE_DIRS = new ArrayList<String>();

    public static void addResources(String res) {
        WORKSPACE_DIRS.add(res);
    }

    public static String getClassPathInWorkspace(String className) {
        for (int i = 0; i < WORKSPACE_DIRS.size(); i++) {
            String path = WORKSPACE_DIRS.get(i).replace('\\', File.separatorChar).replace('/', File.separatorChar)
                          + File.separatorChar + className.replace('.', File.separatorChar) + ".class";
            File file = new File(path);

            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
