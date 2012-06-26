/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.alibaba.hotswap.configuration.HotswapConfiguration;
import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.clinit.ClinitVisitor;
import com.alibaba.hotswap.processor.field.access.FieldAccessVisitor;
import com.alibaba.hotswap.processor.field.holder.FieldAheadVisitor;
import com.alibaba.hotswap.processor.field.holder.FieldHolderInitVisitor;
import com.alibaba.hotswap.processor.field.holder.FieldHolderVisitor;
import com.alibaba.hotswap.processor.front.FieldNodeHolderVisitor;
import com.alibaba.hotswap.processor.front.compile.CompilerErrorVisitor;
import com.alibaba.hotswap.processor.front.vclass.GenerateVClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-6-13
 */
public class HotswapProcessorFactory {

    private static final List<List<Class<? extends BaseClassVisitor>>> hotswap_processor_holder = new ArrayList<List<Class<? extends BaseClassVisitor>>>();

    static {
        // Do not change these processors' order!!!
        int index = 0;
        hotswap_processor_holder.add(new ArrayList<Class<? extends BaseClassVisitor>>());
        hotswap_processor_holder.get(index).add(CompilerErrorVisitor.class);
        hotswap_processor_holder.get(index).add(GenerateVClassVisitor.class);

        index++;
        hotswap_processor_holder.add(new ArrayList<Class<? extends BaseClassVisitor>>());
        hotswap_processor_holder.get(index).add(FieldNodeHolderVisitor.class);

        index++;
        hotswap_processor_holder.add(new ArrayList<Class<? extends BaseClassVisitor>>());
        hotswap_processor_holder.get(index).add(FieldHolderVisitor.class);
        hotswap_processor_holder.get(index).add(FieldHolderInitVisitor.class);
        hotswap_processor_holder.get(index).add(ClinitVisitor.class);

        index++;
        hotswap_processor_holder.add(new ArrayList<Class<? extends BaseClassVisitor>>());
        hotswap_processor_holder.get(index).add(FieldAheadVisitor.class);
        hotswap_processor_holder.get(index).add(FieldAccessVisitor.class);
    }

    @SuppressWarnings("unchecked")
    public static byte[] process(String name, byte[] bytes) throws ClassNotFoundException {
        ClassMeta classMeta = HotswapRuntime.getClassMeta(name);
        classMeta.reset();

        byte[] classBytes = bytes;
        for (int i = 0; i < hotswap_processor_holder.size(); i++) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            ClassVisitor cv = cw;
            if (HotswapConfiguration.TRACE != null && name.indexOf(HotswapConfiguration.TRACE) >= 0) {
                cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
            }

            try {
                for (Class<?> clazz : hotswap_processor_holder.get(i)) {
                    Constructor<ClassVisitor> c = (Constructor<ClassVisitor>) clazz.getConstructor(ClassVisitor.class);
                    cv = c.newInstance(cv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ClassReader cr = new ClassReader(classBytes);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            if (i == 0) {
                // v class
                byte[] vclassBytes = cw.toByteArray();
                classMeta.loadedBytes = vclassBytes;
                classMeta.vClassName = name.replace("/", ".") + HotswapConstants.V_CLASS_PATTERN
                                       + classMeta.loadedIndex;
                synchronized (classMeta) {
                    classMeta.vClass = classMeta.loader.loadClass(classMeta.vClassName);
                }
            } else {
                classBytes = cw.toByteArray();
            }
        }

        return classBytes;
    }
}
