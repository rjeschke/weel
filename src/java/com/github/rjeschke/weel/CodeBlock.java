/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main building block for the Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class CodeBlock
{
    /** List of used locals. */
    ArrayList<Boolean> locals = new ArrayList<Boolean>();
    /** Closure variables get registered at block level. */
    ArrayList<Integer> cvarIndex = new ArrayList<Integer>();
    /** Closure variable name to index mapping. */
    HashMap<String, Integer> cvars = new HashMap<String, Integer>();

    /** The code. */
    ByteList code = new ByteList();
    /** The method writer. */
    final JvmMethodWriter methodWriter;
    /** The class writer. */
    final JvmClassWriter classWriter;
    /** Enclosing WeelFunction (if not STATIC). */
    WeelFunction function;

    /**
     * Constructor.
     * 
     * @param methodWriter
     *            The method writer.
     */
    public CodeBlock(final JvmMethodWriter methodWriter)
    {
        this.classWriter = methodWriter.classWriter;
        this.methodWriter = methodWriter;
    }

    /**
     * Registers a local variable.
     * 
     * @return The local variable index.
     */
    int registerLocal()
    {
        for (int i = 0; i < this.locals.size(); i++)
        {
            if (!this.locals.get(i).booleanValue())
            {
                this.locals.set(i, true);
                return i;
            }
        }
        final int ret = this.locals.size();
        this.locals.add(true);
        return ret;
    }

    /**
     * Unregisters a local variable.
     * 
     * @param index
     *            The variable index.
     */
    void unregisterLocal(final int index)
    {
        this.locals.set(index, false);
    }

    /**
     * Gets the current code program counter.
     * 
     * @return The PC.
     */
    int getPc()
    {
        return this.code.size();
    }

    /**
     * Writes a short at the specified position.
     * 
     * @param pc
     *            The position.
     * @param value
     *            The value.
     */
    void writeShortAt(final int pc, final int value)
    {
        this.code.setShort(pc, value);
    }

    /**
     * Writes a jump instruction.
     * 
     * @param op
     *            The instruction.
     * @param value
     *            The offset.
     * @return The program counter of the jump offset.
     */
    int writeJmp(final int op, final int value)
    {
        final int ret;
        this.code.add(op);
        ret = this.code.size();
        this.code.addShort(value);
        return ret;
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

    /**
     * Calls a runtime function.
     * 
     * @param name
     *            The name.
     */
    void callRuntime(final String name)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", name, "()V"));
    }

    /**
     * Calls a runtime function.
     * 
     * @param name
     *            The name.
     * @param descriptor
     *            The descriptor.
     */
    void callRuntime(final String name, final String descriptor)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", name, descriptor));
    }

    /**
     * Calls a runtime function.
     * 
     * @param name
     *            The name.
     * @param arg0
     *            Integer argument.
     */
    void callRuntime(final String name, final int arg0)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.ldcInt(arg0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", name, "(I)V"));
    }

    /**
     * Writes a stackCall instruction.
     * 
     * @param arguments
     *            Number of arguments.
     * @param wantsValue
     *            Do we need a return value?
     */
    void doStackcall(final int arguments, final boolean wantsValue)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.ldcInt(arguments);
        this.code.add(wantsValue ? JvmOp.ICONST_1 : JvmOp.ICONST_0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "stackCall", "(IZ)V"));
    }

    /**
     * Loads a String.
     * 
     * @param value
     *            The String.
     */
    void load(final String value)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.ldc(this.classWriter.addConstant(new JvmConstant(
                JvmConstant.CONSTANT_String, this.classWriter
                        .addConstant(new JvmConstant(value)))));
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "load",
                "(Ljava/lang/String;)V"));
    }

    /**
     * Loads a double. Performs checks to minimize the amount of used storage.
     * 
     * @param value
     *            The value.
     */
    void load(final double value)
    {
        final int iVal = (int) Math.floor(value);
        if (iVal == value)
        {
            this.load(iVal);
        }
        else
        {
            this.code.add(JvmOp.ALOAD_0);
            final float check = (float) value;
            if (Double.compare(check, value) == 0)
            {
                this.ldc(this.classWriter.addConstant(new JvmConstant(check)));
                this.code.add(JvmOp.INVOKEVIRTUAL);
                this.code.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.Runtime", "load", "(F)V"));
            }
            else
            {
                this.code.add(JvmOp.LDC2_W);
                this.code.addShort(this.classWriter
                        .addConstant(new JvmConstant(value)));
                this.code.add(JvmOp.INVOKEVIRTUAL);
                this.code.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.Runtime", "load", "(D)V"));
            }
        }
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
     * Loads a value as an integer (onto the Weel stack).
     * 
     * @param value
     *            The value.
     */
    void load(final int value)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.ldcInt(value);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "load", "(I)V"));
    }

    /**
     * Closes this code block, writing lead-in and lead-out.
     */
    // TODO ... there's so much to do here
    void closeBlock()
    {
        final JvmClassWriter cw = this.methodWriter.classWriter;
        final ByteList old = this.code;
        this.code = this.methodWriter.code;

        // Write header
        this.code.add(JvmOp.ALOAD_0);
        this.ldcInt(0);
        this.ldcInt(this.locals.size());
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(cw.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "openFrame", "(II)V"));

        // Copy old code
        for (int i = 0; i < old.size(); i++)
            this.code.add(old.get(i));

        // Write footer
        this.code.add(JvmOp.ALOAD_0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(cw.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "closeFrame", "()V"));
        this.code.add(JvmOp.RETURN);
    }
}
