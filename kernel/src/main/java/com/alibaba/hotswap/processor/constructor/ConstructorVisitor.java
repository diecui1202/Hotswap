/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.constructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.MethodMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.constructor.modifier.ConstructorInvokeModifier;
import com.alibaba.hotswap.processor.constructor.modifier.ConstructorLVTAdjustModifier;
import com.alibaba.hotswap.processor.constructor.modifier.FieldHolderInitModifier;
import com.alibaba.hotswap.runtime.HotswapMethodIndexHolder;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapMethodUtil;

/**
 * @author yong.zhuy 2012-6-13
 */
public class ConstructorVisitor extends BaseClassVisitor {

    private List<String>            initKeys  = new ArrayList<String>();
    private Map<String, MethodNode> initNodes = new HashMap<String, MethodNode>();

    public ConstructorVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(HotswapConstants.INIT)) {
            MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);
            String mk = HotswapMethodUtil.getMethodKey(name, desc);
            initKeys.add(mk);
            initNodes.put(mk, mn);
            return mn;
        }

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        mv = new ConstructorInvokeModifier(mv, access, name, desc);
        return mv;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visitEnd() {
        if (!isInterface) {
            ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
            if (!HotswapRuntime.getClassInitialized(className)) {
                // The first time
                for (String mk : initKeys) {
                    MethodNode node = initNodes.get(mk);
                    node.accept(cv);

                    classMeta.primaryInitNodes.put(mk, node);
                    classMeta.primaryInitKeyList.add(mk);

                    MethodMeta mm = new MethodMeta(
                                                   node.access,
                                                   node.name,
                                                   node.desc,
                                                   node.signature,
                                                   (String[]) (node.exceptions == null ? null : node.exceptions.toArray(new String[] {})));
                    classMeta.refreshInitMeta(mm, false);
                }
                addEmptyUniformConstructor();
            } else {
                // Reload
                for (String mk : classMeta.primaryInitKeyList) {
                    MethodNode node = initNodes.remove(mk);
                    if (node != null) {
                        MethodMeta mm = new MethodMeta(
                                                       node.access,
                                                       node.name,
                                                       node.desc,
                                                       node.signature,
                                                       (String[]) (node.exceptions == null ? null : node.exceptions.toArray(new String[] {})));
                        classMeta.refreshInitMeta(mm, false);
                    }

                    node = classMeta.primaryInitNodes.get(mk);
                    node.accept(cv);
                }

                addUniformConstructor(classMeta);
            }

            Iterator<Entry<String, MethodMeta>> iter = classMeta.initMetas.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, MethodMeta> entry = iter.next();
                MethodMeta mm = entry.getValue();
                if (classMeta.loadedIndex > mm.loadedIndex && !classMeta.primaryInitKeyList.contains(mm.getKey())) {
                    iter.remove();
                }
            }
        }
        super.visitEnd();
    }

    private void addEmptyUniformConstructor() {
        int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNTHETIC;
        String name = HotswapConstants.INIT;
        String desc = HotswapConstants.UNIFORM_CONSTRUCTOR_DESC;

        MethodVisitor hotswapInit = cv.visitMethod(access, name, desc, null, null);
        GeneratorAdapter hotswapInitAdapter = new GeneratorAdapter(hotswapInit, access, name, desc);
        hotswapInitAdapter.visitCode();
        hotswapInitAdapter.push(this.className);
        hotswapInitAdapter.loadArg(1);
        hotswapInitAdapter.invokeStatic(Type.getType(HotswapMethodUtil.class),
                                        Method.getMethod("Throwable noSuchMethodError(String, int)"));
        hotswapInitAdapter.throwException();
        hotswapInitAdapter.endMethod();
    }

    @SuppressWarnings({ "unchecked" })
    private void addUniformConstructor(ClassMeta classMeta) {
        int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNTHETIC;
        String name = HotswapConstants.INIT;
        String desc = HotswapConstants.UNIFORM_CONSTRUCTOR_DESC;

        MethodVisitor hotswapInit = new ConstructorInvokeModifier(
                                                                  new FieldHolderInitModifier(
                                                                                              cv.visitMethod(access,
                                                                                                             name,
                                                                                                             desc,
                                                                                                             null, null),
                                                                                              access, name, desc,
                                                                                              className), access, name,
                                                                  desc);
        GeneratorAdapter hotswapInitAdapter = new GeneratorAdapter(hotswapInit, access, name, desc);

        hotswapInitAdapter.visitCode();

        TreeMap<MethodMeta, MethodNode> initMethodMap = new TreeMap<MethodMeta, MethodNode>(
                                                                                            new ConstructorIndexComparator());

        for (MethodNode node : initNodes.values()) {
            MethodMeta meta = new MethodMeta(node.access, node.name, node.desc, node.signature,
                                             ((String[]) node.exceptions.toArray(new String[node.exceptions.size()])));
            meta.setIndex(HotswapMethodIndexHolder.getMethodIndex(className, node.name, node.desc));
            classMeta.refreshInitMeta(meta, true);
            initMethodMap.put(meta, node);
        }

        List<MethodMeta> keys = new ArrayList<MethodMeta>(initMethodMap.keySet());
        List<MethodNode> values = new ArrayList<MethodNode>(initMethodMap.values());

        Label defaultLabel = new Label();
        int[] indexes = new int[keys.size()];
        Label[] labels = new Label[keys.size()];

        for (int i = 0; i < keys.size(); i++) {
            indexes[i] = keys.get(i).getIndex();
            labels[i] = new Label();
        }

        for (int i = 0; i < values.size(); i++) {
            MethodNode node = values.get(i);
            for (int j = 0; j < node.tryCatchBlocks.size(); j++) {
                ((TryCatchBlockNode) node.tryCatchBlocks.get(j)).accept(hotswapInitAdapter);
            }
        }

        hotswapInitAdapter.loadArg(1);
        hotswapInitAdapter.visitLookupSwitchInsn(defaultLabel, indexes, labels);

        for (int i = 0; i < keys.size(); i++) {
            MethodMeta methodMeta = keys.get(i);
            hotswapInitAdapter.visitLabel(labels[i]);
            MethodNode node = values.get(i);

            storeArgs(hotswapInitAdapter, hotswapInit, methodMeta);
            MethodVisitor methodVisitor = new ConstructorLVTAdjustModifier(hotswapInit, 3);

            node.instructions.accept(methodVisitor);

            for (int j = 0; j < (node.localVariables == null ? 0 : node.localVariables.size()); j++) {
                ((LocalVariableNode) node.localVariables.get(j)).accept(methodVisitor);
            }
        }
        hotswapInitAdapter.mark(defaultLabel);

        hotswapInitAdapter.push(this.className);
        hotswapInitAdapter.loadArg(1);
        hotswapInitAdapter.invokeStatic(Type.getType(HotswapMethodUtil.class),
                                        Method.getMethod("Throwable noSuchMethodError(String, int)"));
        hotswapInitAdapter.throwException();
        hotswapInitAdapter.endMethod();
    }

    private void storeArgs(GeneratorAdapter adapter, MethodVisitor hotswapInit, MethodMeta methodMeta) {
        Type[] argTypes = Type.getArgumentTypes(methodMeta.desc);

        if (argTypes.length == 0) {
            return;
        }

        adapter.loadArg(2);// Object[]

        int nextIndex = 4;
        for (int i = 0; i < argTypes.length; i++) {
            adapter.dup();
            adapter.push(i);
            adapter.arrayLoad(Type.getType(Object.class));// Object[i]
            adapter.unbox(argTypes[i]);
            hotswapInit.visitVarInsn(argTypes[i].getOpcode(Opcodes.ISTORE), nextIndex);
            nextIndex += argTypes[i].getSize();
        }

        adapter.pop();
    }

    class ConstructorIndexComparator implements Comparator<MethodMeta> {

        @Override
        public int compare(MethodMeta o1, MethodMeta o2) {
            return o1.getIndex() == o2.getIndex() ? 0 : (o1.getIndex() < o2.getIndex() ? -1 : 1);
        }
    }
}
