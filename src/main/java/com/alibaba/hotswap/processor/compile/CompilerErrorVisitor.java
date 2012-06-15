/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.compile;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.alibaba.hotswap.exception.HotswapException;

/**
 * Force hotswap when eclipse compile a Java source file which has compiler errors.
 * 
 * @author yong.zhuy 2012-5-24 12:17:22
 */
public class CompilerErrorVisitor extends ClassVisitor {

    public CompilerErrorVisitor(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        return new GeneratorAdapter(mv, access, name, desc) {

            private int status = 0;

            @Override
            public void visitCode() {
                super.visitCode();
                status = 1;
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (status == 1 && opcode == Opcodes.NEW) {
                    status = 2;
                } else {
                    status = 0;
                }
                super.visitTypeInsn(opcode, type);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                if (status == 4 && opcode == Opcodes.INVOKESPECIAL) {
                    status = 5;
                } else {
                    status = 0;
                }
                super.visitMethodInsn(opcode, owner, name, desc);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (status == 3) {
                    status = 4;
                } else {
                    status = 0;
                }
                super.visitLdcInsn(cst);
            }

            @Override
            public void visitInsn(int opcode) {
                if (status == 2 && opcode == Opcodes.DUP) {
                    status = 3;
                } else if (status == 5 && opcode == Opcodes.ATHROW) {
                    status = 6;
                } else {
                    status = 0;
                }

                super.visitInsn(opcode);
            }

            @Override
            public void visitEnd() {
                if (status == 6) {
                    throw new HotswapException("Class file is compiled from Java source file which has compile error");
                }
                super.visitEnd();
            }
        };
    }
}
