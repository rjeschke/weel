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
     * Flag indicating that our function has at least one exit statement.
     * Relevant only for automatically typed anonymous functions.
     */
    boolean hasExit;
    /** Current stack position. */
    int currentStack;
    /** Maximum stack depth. */
    int maxStack;

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
                "com.github.rjeschke.weel.WeelRuntime", name, "()V"));
        this.autoStack(name, 0);
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
                "com.github.rjeschke.weel.WeelRuntime", name, descriptor));
        this.autoStack(name, 0);
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
                "com.github.rjeschke.weel.WeelRuntime", name, descriptor));
        this.autoStack(name, arg0);
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
                "com.github.rjeschke.weel.WeelRuntime", name, "(I)V"));
        this.autoStack(name, arg0);
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
                "com.github.rjeschke.weel.WeelRuntime", "stackCall", "(IZ)V"));
        if (wantsValue)
            this.push();
        this.pop(arguments);
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
                "com.github.rjeschke.weel.WeelRuntime", "load",
                "(Ljava/lang/String;)V"));
        this.push();
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
                        "com.github.rjeschke.weel.WeelRuntime", "load", "(F)V"));
            }
            else
            {
                this.code.add(JvmOp.LDC2_W);
                this.code.addShort(this.classWriter
                        .addConstant(new JvmConstant(value)));
                this.code.add(JvmOp.INVOKEVIRTUAL);
                this.code.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load", "(D)V"));
            }
            this.push();
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
                "com.github.rjeschke.weel.WeelRuntime", "load", "(I)V"));
        this.push();
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
                "com.github.rjeschke.weel.WeelRuntime", "specialCall",
                "(Ljava/lang/String;IZ)V"));
        if (shouldReturn)
            this.push();
        this.pop(args + 1);
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
                "com.github.rjeschke.weel.WeelRuntime", "openFrame", "(II)V"));

        // Copy old code
        for (int i = 0; i < old.size(); i++)
            this.code.add(old.get(i));

        // Write footer
        this.code.add(JvmOp.ALOAD_0);
        this.ldcInt(this.maxStack);
        this.code.add(JvmOp.INVOKEVIRTUAL);
        this.code.addShort(cw.addMethodRefConstant(
                "com.github.rjeschke.weel.WeelRuntime", (this.function != null
                        && this.function.returnsValue ? "closeFrameRet"
                        : "closeFrame"), "(I)V"));
        this.code.add(JvmOp.RETURN);
        if (this.function != null && this.function.returnsValue)
            this.pop();
    }

    /**
     * Pushes the stack counter.
     */
    public void push()
    {
        this.push(1);
    }

    /**
     * Pushes the stack counter.
     * 
     * @param num
     *            Number of pushes.
     */
    public void push(final int num)
    {
        this.maxStack = Math.max(this.currentStack += num, this.maxStack);
    }

    /**
     * Pops the stack counter.
     */
    public void pop()
    {
        this.pop(1);
    }

    /**
     * Pops the stack counter.
     * 
     * @param num
     *            Number of pops.
     */
    public void pop(final int num)
    {
        this.currentStack = Math.max(0, this.currentStack - num);
    }

    /**
     * Performs auto stack depth evaluation.
     * 
     * @param ins
     *            The runtime instruction.
     * @param val
     *            The argument for pop(int).
     */
    void autoStack(final String ins, int val)
    {
        final RuntimeOp op = RuntimeOp.fromValue(ins);
        if (op != RuntimeOp.NONE)
        {
            if (op == RuntimeOp.pop)
            {
                this.pop(val);
            }
            else
            {
                if (op.getAmount() < 0)
                    this.pop(-op.getAmount());
                else
                    this.push(op.getAmount());
            }
        }
    }

    /**
     * Enumeration for auto stack checking.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    static enum RuntimeOp
    {
        NONE(0),

        load(1), loadfunc(1), lloc(1), lglob(1), linenv(1),

        sloc(-1), sglob(-1), sinenv(-1),

        pop1(-1), pop(0), sdup(1), sdup2(2), sdups(1),

        testpoptrue(-1), testpopfalse(-1), popboolean(-1),

        cmp(-2), cmpequal(-2),

        cmpeq(-1), cmpne(-1), cmple(-1), cmplt(-1), cmpge(-1), cmpgt(-1),

        doforeach(2), strcat(-1), mapcat(-1),

        add(-1), sub(-1), mul(-1), mod(-1), div(-1), and(-1),

        or(-1), xor(-1),

        createmap(1), getmap(-2), setmap(-3), appendmap(-2),

        createvirtual(1);

        /** Amount to push or pop. */
        private final int amount;
        /** String to enum mapping. */
        private final static HashMap<String, RuntimeOp> map = new HashMap<String, RuntimeOp>();

        /**
         * Ctor.
         * 
         * @param a
         *            Amount to pop of push.
         */
        private RuntimeOp(final int a)
        {
            this.amount = a;
        }

        static
        {
            for (final RuntimeOp o : RuntimeOp.values())
            {
                map.put(o.toString(), o);
            }
        }

        /**
         * Returns an enum for a String.
         * 
         * @param value
         *            The String.
         * @return The enum.
         */
        public static RuntimeOp fromValue(final String value)
        {
            final RuntimeOp r = map.get(value.toLowerCase());
            return r != null ? r : NONE;
        }

        /**
         * Gets the amount to pop or push.
         * 
         * @return The amount.
         */
        public int getAmount()
        {
            return this.amount;
        }
    }
}
