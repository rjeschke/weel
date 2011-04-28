/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.Modifier;

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

    JvmMethodWriter(final JvmClassWriter classWriter, final String methodName,
            final String descriptor)
    {
        this.classWriter = classWriter;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    public ByteList getCodeList()
    {
        return this.code;
    }

    public void wLoad(final double value)
    {
        this.code.add(JvmOp.ALOAD_0);
        final int iVal = (int) Math.floor(value);
        if (iVal == value)
        {
            switch (iVal)
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
            {
                final int ci = this.classWriter.addConstant(new JvmConstant(
                        iVal));
                if (ci > 255)
                {
                    this.code.add(JvmOp.LDC_W);
                    this.code.addShort(ci);
                }
                else
                {
                    this.code.add(JvmOp.LDC);
                    this.code.add(ci);
                }
            }
            }
            this.code.add(JvmOp.INVOKEVIRTUAL);
            this.code.addShort(this.classWriter.addMethodRefConstant(
                    "com.github.rjeschke.weel.Runtime", "load", "(I)V"));
        }
    }
}
