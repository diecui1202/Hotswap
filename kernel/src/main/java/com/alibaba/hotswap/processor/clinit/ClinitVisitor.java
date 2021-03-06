/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.clinit;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;

/**
 * Replace <code>&lt;clinit&gt;</code> to <code>__$$hotswap_clinit$$__</code> for when the class bytes are been
 * redefined, call <code>&lt;clinit&gt;</code> again.
 * 
 * @author yong.zhuy 2012-6-13 14:25:36
 */
public class ClinitVisitor extends BaseClassVisitor {

    private boolean hasClinitMethod;
    private boolean isInterface;

    public ClinitVisitor(ClassVisitor cv){
        super(cv);
        hasClinitMethod = false;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        isInterface = ((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(HotswapConstants.CLINIT)) {
            hasClinitMethod = true;

            if (!isInterface) {
                // If it is a class, then alias clinit to hotswap_clinit
                name = HotswapConstants.HOTSWAP_CLINIT;
                access = access + Opcodes.ACC_PUBLIC;
            }

            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new ClinitModifier(mv, access, name, desc, className, isInterface);
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {
        // If no clinit method, then add it
        if (!hasClinitMethod) {
            if (!isInterface) {
                int access = Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC;
                String name = HotswapConstants.HOTSWAP_CLINIT;
                String desc = "()V";
                MethodVisitor mv = super.visitMethod(access, name, desc, null, null);
                if (mv != null) {
                    mv.visitCode();
                    mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V");
                    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, HotswapConstants.STATIC_FIELD_HOLDER,
                                      "Ljava/util/concurrent/ConcurrentHashMap;");

                    mv.visitInsn(Opcodes.RETURN);
                    mv.visitMaxs(2, 0);
                    mv.visitEnd();
                }

                generateClinit();
            } else {
                generateDefaultClinit();
            }
        } else {
            if (!isInterface) {
                generateClinit();
            }
        }

        super.visitEnd();
    }

    /**
     * Generate <code>&lt;clinit&gt;</code>, call <code>__$$hotswap_clinit$$__</code>
     */
    private void generateClinit() {
        MethodVisitor clinit = cv.visitMethod(Opcodes.ACC_STATIC, HotswapConstants.CLINIT, "()V", null, null);
        if (clinit != null) {
            clinit.visitCode();

            clinit.visitMethodInsn(Opcodes.INVOKESTATIC, className, HotswapConstants.HOTSWAP_CLINIT, "()V");
            clinit.visitInsn(Opcodes.RETURN);
            clinit.visitMaxs(0, 0);
            clinit.visitEnd();
        }
    }

    /**
     * Generate <code>&lt;clinit&gt;</code> method with default body
     */
    private void generateDefaultClinit() {
        MethodVisitor clinit = cv.visitMethod(Opcodes.ACC_STATIC, HotswapConstants.CLINIT, "()V", null, null);

        if (clinit != null) {
            clinit.visitCode();

            clinit.visitInsn(Opcodes.RETURN);
            clinit.visitMaxs(0, 0);
            clinit.visitEnd();
        }
    }
}
