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
import java.net.URLClassLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.alibaba.hotswap.configuration.HotswapConfiguration;
import com.alibaba.hotswap.exception.HotswapException;
import com.alibaba.hotswap.processor.jdk.classloader.ClassLoaderVisitor;
import com.alibaba.hotswap.processor.jdk.classloader.URLClassLoaderVisitor;
import com.alibaba.hotswap.reload.ReloadChecker;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-5-18 13:05:28
 */
public class AgentInstall {

    public static void premain(String agentArgs, Instrumentation inst) {
        HotswapRuntime.holdInst(inst);

        parseArgs(agentArgs);

        redefineClassLoader(inst);

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
            if (trace.equalsIgnoreCase("true")) {
                HotswapConfiguration.TRACE = true;
            }
        }
    }

    public static void redefineClassLoader(Instrumentation inst) {
        try {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassLoaderVisitor clv = new ClassLoaderVisitor(cw);
            InputStream is = ClassLoader.getSystemResourceAsStream(ClassLoader.class.getName().replace('.', '/')
                                                                   + ".class");
            ClassReader cr = new ClassReader(is);
            cr.accept(clv, 0);
            ClassDefinition definitions = new ClassDefinition(ClassLoader.class, cw.toByteArray());
            inst.redefineClasses(definitions);
        } catch (Exception e) {
            throw new HotswapException("redefine class error, name: " + ClassLoader.class.getName(), e);
        }

        try {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            URLClassLoaderVisitor clv = new URLClassLoaderVisitor(cw);
            InputStream is = ClassLoader.getSystemResourceAsStream(URLClassLoader.class.getName().replace('.', '/')
                                                                   + ".class");
            ClassReader cr = new ClassReader(is);
            cr.accept(clv, 0);
            ClassDefinition definitions = new ClassDefinition(URLClassLoader.class, cw.toByteArray());
            inst.redefineClasses(definitions);
        } catch (Exception e) {
            throw new HotswapException("redefine class error, name: " + URLClassLoader.class.getName(), e);
        }
    }

    private static void startReloadChecker() {
        Thread reloader = new Thread(new ReloadChecker());
        reloader.setName("Hotswap Reloader");
        reloader.setDaemon(true);

        reloader.start();
    }
}
