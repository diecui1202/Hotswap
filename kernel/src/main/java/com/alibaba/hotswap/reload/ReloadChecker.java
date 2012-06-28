/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.reload;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * Check the class file to decide reload
 * 
 * @author yong.zhuy 2012-5-18 12:32:17
 */
public class ReloadChecker implements Runnable {

    @Override
    public void run() {
        while (true) {
            Map<String, ClassMeta> classMetas = new HashMap<String, ClassMeta>();
            classMetas.putAll(HotswapRuntime.CLASS_METAS);

            for (Entry<String, ClassMeta> entry : classMetas.entrySet()) {
                ClassMeta meta = entry.getValue();
                if (meta != null && meta.path != null) {
                    File f = new File(meta.path);
                    if (f.lastModified() > meta.lastModified) {
                        try {
                            synchronized (meta) {
                                HotswapRuntime.redefineClass(meta);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
        }
    }
}
