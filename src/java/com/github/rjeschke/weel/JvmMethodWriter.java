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
    /** The class writer. */
    final JvmClassWriter classWriter;
    /** This methods's name. */
    String methodName;
    /** This method's descriptor. */
    final String descriptor;
    /** This method's code. */
    ByteList code = new ByteList();
    /** Maximum stack used. */
    int maxStack = 4;
    /** Maximum locals used. */
    int maxLocals = 1;
    /** The name constant pool index. */
    int nameIndex;
    /** The descriptor constant pool index. */
    int descriptorIndex;
    /** The access mode. */
    int access = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;

    /**
     * Constructor.
     * 
     * @param classWriter
     *            The class writer.
     * @param methodName
     *            The method name.
     * @param descriptor
     *            The descriptor.
     */
    JvmMethodWriter(final JvmClassWriter classWriter, final String methodName,
            final String descriptor)
    {
        this.classWriter = classWriter;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    /**
     * Loads an integer (as a constant).
     * 
     * @param value
     *            The value.
     */
    void ldcInt(final int value)
    {
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
    }

    /**
     * Loads a constant.
     * 
     * @param index
     *            The constant pool index.
     */
    void ldc(final int index)
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
}
