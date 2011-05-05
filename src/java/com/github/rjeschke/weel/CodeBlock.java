/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.rjeschke.weel.Variable.Type;

/**
 * Main building block for the Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class CodeBlock
{
    /** List of used locals. */
    ArrayList<Boolean> locals = new ArrayList<Boolean>();
    /** Closure variables indices. */
    ArrayList<Integer> cvarIndex = new ArrayList<Integer>();
    /** Variable index to closure index mapping. */
    HashMap<Integer, Integer> cvarMap = new HashMap<Integer, Integer>();
    /** Closure variable name to index mapping. */
    final HashMap<String, Integer> cvars = new HashMap<String, Integer>();

    /** The code. */
    ByteList code = new ByteList();
    /** The method writer. */
    JvmMethodWriter methodWriter;
    /** The class writer. */
    JvmClassWriter classWriter;
    /** Enclosing WeelFunction (if not STATIC). */
    WeelFunction function;
    /** Flag indicating that we're an anonymous function. */
    boolean isAnonymousFunction;
    /** Flag indicating that we're an anonymous function using alternate syntax. */
    boolean isAlternateSyntax;
    /** Flag indicating that our function has at least one return statement. */
    boolean hasReturn;

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
     * Constructor.
     * 
     * @param methodWriter
     *            The method writer.
     */
    CodeBlock()
    {
        //
    }

    /**
     * Sets the method writer.
     * 
     * @param methodWriter
     *            The method writer.
     */
    void setMethodWriter(final JvmMethodWriter methodWriter)
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
     * Registers or gets a closure variable.
     * 
     * @param var
     *            The variable.
     * @return The index or <code>-1</code>
     */
    int getCvar(final Variable var)
    {
        if (var.type == Type.NONE || var.type == Type.GLOBAL)
            return -1;
        final int vi = var.type == Type.LOCAL ? var.index : -var.index - 1;
        final Integer idx = this.cvarMap.get(vi);
        if (idx != null)
            return idx;
        final int num = this.cvarIndex.size();
        this.cvarIndex.add(vi);
        this.cvarMap.put(vi, num);
        return num;
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
     * @param descriptor
     *            The descriptor.
     * @param arg0
     *            Integer argument.
     */
    void callRuntime(final String name, final String descriptor, final int arg0)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.ldcInt(arg0);
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
        this.ldcStr(value);
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
     * Loads a String (as a constant).
     * 
     * @param value
     *            The value.
     */
    void ldcStr(final String value)
    {
        this.ldc(this.classWriter.addConstant(new JvmConstant(
                JvmConstant.CONSTANT_String, this.classWriter
                        .addConstant(new JvmConstant(value)))));
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
     * Writes a specialCall instruction.
     * 
     * @param name
     *            The name of the function.
     * @param args
     *            Thenumber of arguments.
     * @param shouldReturn
     *            Flags indicating that we need a return value.
     */
    void writeSpecialCall(final String name, final int args,
            final boolean shouldReturn)
    {
        this.code.add(JvmOp.ALOAD_0);
        this.ldcStr(name + "#" + (args + 1));
        this.ldcInt(args);
        this.ldcInt(shouldReturn ? 1 : 0);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.Runtime", "specialCall",
                "(Ljava/lang/String;IZ)V"));
    }

    /**
     * Closes this code block, writing lead-in and lead-out.
     */
    void closeBlock()
    {
        final JvmClassWriter cw = this.methodWriter.classWriter;
        final ByteList old = this.code;
        this.code = this.methodWriter.code;

        // Write header
        this.code.add(JvmOp.ALOAD_0);
        if (this.function != null)
        {
            this.ldcInt(this.function.arguments);
            this.ldcInt(this.locals.size() - this.function.arguments);
        }
        else
        {
            this.ldcInt(0);
            this.ldcInt(this.locals.size());
        }
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
                "com.github.rjeschke.weel.Runtime", (this.function != null
                        && this.function.returnsValue ? "closeFrameRet"
                        : "closeFrame"), "()V"));
        this.code.add(JvmOp.RETURN);
    }
}
