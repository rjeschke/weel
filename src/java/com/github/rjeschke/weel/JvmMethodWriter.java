/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Method writer specialized for Weel.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class JvmMethodWriter
{
    private final JvmClassWriter classWriter;
    final String methodName;
    final String descriptor;
    ByteList code = new ByteList();
    int maxStack = 4, maxLocals = 1;
    int nameIndex, descriptorIndex;
    int access = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;
    ArrayList<Integer> relocs = new ArrayList<Integer>();
    
    JvmMethodWriter(final JvmClassWriter classWriter, final String methodName,
            final String descriptor)
    {
        this.classWriter = classWriter;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    @Deprecated
    public ByteList getCodeList()
    {
        return this.code;
    }

    public void writeJmp(final int op, final int value)
    {
        this.code.add(op);
        this.relocs.add(this.code.size());
        this.code.addShort(value);
    }
    
    private void ldc(final int index)
    {
        if (index > 255)
        {
            this.code.add(JvmOp.LDC_W);
            this.code.addShort(index);
        }
        else
        {
            this.code.add(JvmOp.LDC);
            this.code.add(index);
        }
    }
    
    public void callRuntime(final String name)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", name, "()V"));
    }
    
    public void dLoad(final double value)
    {
        final int iVal = (int) Math.floor(value);
        if (iVal == value)
        {
            this.iLoad(iVal);
        }
        else
        {
            this.code.add(JvmOp.ALOAD_0);
            // TODO check if we can use a float instead of a double
            this.ldc(this.classWriter.addConstant(new JvmConstant(value)));
            this.code.add(JvmOp.INVOKEVIRTUAL);
            this.code.addShort(this.classWriter.addMethodRefConstant(
                    "com.github.rjeschke.weel.Runtime", "load", "(D)V"));
        }
    }

    public void iLoad(final int value)
    {
        this.code.add(JvmOp.ALOAD_0);
        switch (value)
        {
        case -1:
            this.code.add(JvmOp.ICONST_M1);
            break;
        case 0:
            this.code.add(JvmOp.ICONST_0);
            break;
        case 1:
            this.code.add(JvmOp.ICONST_1);
            break;
        case 2:
            this.code.add(JvmOp.ICONST_2);
            break;
        case 3:
            this.code.add(JvmOp.ICONST_3);
            break;
        case 4:
            this.code.add(JvmOp.ICONST_4);
            break;
        case 5:
            this.code.add(JvmOp.ICONST_5);
            break;
        default:
            this.ldc(this.classWriter.addConstant(new JvmConstant(value)));
            break;
        }
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "load", "(I)V"));
    }
}
