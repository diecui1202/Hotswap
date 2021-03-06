/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap;

import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.Map.Entry;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import com.alibaba.hotswap.configuration.HotswapConfiguration;
import com.alibaba.hotswap.exception.HotswapException;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.jdk.JdkClassProcessorFactory;
import com.alibaba.hotswap.reload.ReloadChecker;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-5-18 13:05:28
 */
public class AgentInstall {

    public static void premain(String agentArgs, Instrumentation inst) {
        HotswapRuntime.holdInst(inst);

        parseArgs(agentArgs);

        redifineJdkClasses(inst);

        startReloadChecker();
    }

    private static void parseArgs(String agentArgs) {
        if (agentArgs != null) {
            String[] slots = agentArgs.split("\\|");
            for (String slot : slots) {
                if ("verbose".equals(slot)) {
                    //
                } else {
                    String[] resources = slot.split("=");
                    if (resources.length == 2 && "resources".equals(resources[0])) {
                        String res = resources[1];
                        for (String r : res.split(";")) {
                            HotswapConfiguration.addResources(r);
                        }
                    }
                }
            }
        }

        String trace = System.getProperty("hotswap.trace");
        if (trace != null) {
            HotswapConfiguration.TRACE = trace;
        }

        String autoReload = System.getProperty("hotswap.autoreload");
        if (autoReload != null && autoReload.equalsIgnoreCase("false")) {
            HotswapConfiguration.AUTO_RELOAD = false;
        }
    }

    public static void redifineJdkClasses(Instrumentation inst) {
        for (Entry<Class<?>, Class<? extends BaseClassVisitor>> entry : JdkClassProcessorFactory.jdk_class_processor_holder.entrySet()) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = cw;
            String name = entry.getKey().getName();

            try {
                Constructor<? extends BaseClassVisitor> c = entry.getValue().getConstructor(ClassVisitor.class);
                cv = c.newInstance(cv);
                InputStream is = ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class");
                ClassReader cr = new ClassReader(is);
                cr.accept(cv, 0);
                ClassDefinition definitions = new ClassDefinition(entry.getKey(), cw.toByteArray());
                inst.redefineClasses(definitions);
            } catch (Exception e) {
                throw new HotswapException("redefine class error, name: " + name, e);
            }
        }
    }

    private static void startReloadChecker() {
        if (HotswapConfiguration.AUTO_RELOAD) {
            Thread reloader = new Thread(new ReloadChecker());
            reloader.setName("Hotswap Reloader");
            reloader.setDaemon(true);

            reloader.start();
        }
    }
}
