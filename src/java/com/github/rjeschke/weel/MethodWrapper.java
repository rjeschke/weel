/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.Method;

/**
 * Class responsible for wrapping 'nice' methods.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class MethodWrapper
{
    /** The ClassWriter. */
    JvmClassWriter classWriter;

    /**
     * Constructor.
     * 
     * @param name
     *            The class name.
     */
    public MethodWrapper(final String name)
    {
        this.classWriter = new JvmClassWriter("com.github.rjeschke.wrappers."
                + name);
    }

    /**
     * Gets the class name of this class writer's class.
     * 
     * @return The class name.
     */
    public String getClassName()
    {
        return this.classWriter.className;
    }

    /**
     * Wraps a method.
     * 
     * @param m
     *            The method to wrap.
     * @return The gerated method name.
     */
    public String wrap(final Method m, final WeelFunction func)
    {
        for (int i = 0; i < m.getParameterTypes().length; i++)
        {
            if (m.getParameterTypes()[i] == com.github.rjeschke.weel.WeelRuntime.class)
                func.arguments--;
        }

        final String mname = "wrap$" + m.getName() + "$" + func.arguments;
        final JvmMethodWriter mw = this.classWriter.createMethod(mname,
                "(Lcom/github/rjeschke/weel/WeelRuntime;)V");
        final ByteList bl = mw.code;

        mw.maxStack = 3;
        bl.add(JvmOp.ALOAD_0);
        mw.ldcInt(func.arguments);
        mw.ldcInt(0);
        bl.add(JvmOp.INVOKEVIRTUAL);
        bl.addShort(this.classWriter.addMethodRefConstant(
                "com.github.rjeschke.weel.WeelRuntime", "openFrame", "(II)V"));

        int sp = m.getParameterTypes().length > 0 ? 2 : 0;
        final boolean returns;

        if (m.getReturnType() != void.class)
        {
            returns = true;
            bl.add(JvmOp.ALOAD_0);
            sp++;
        }
        else
        {
            returns = false;
        }

        for (int i = 0, p = 0; i < m.getParameterTypes().length; i++)
        {
            final Class<?> t = m.getParameterTypes()[i];
            if (t == com.github.rjeschke.weel.WeelRuntime.class)
            {
                sp++;
                bl.add(JvmOp.ALOAD_0);
                continue;
            }
            bl.add(JvmOp.ALOAD_0);
            mw.ldcInt(p++);
            bl.add(JvmOp.INVOKEVIRTUAL);
            if (t == double.class)
            {
                sp += 2;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getNumberLocal",
                        "(I)D"));
            }
            else if (t == int.class)
            {
                sp += 2;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getNumberLocal",
                        "(I)D"));
                bl.add(JvmOp.D2I);
            }
            else if (t == boolean.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getBooleanLocal",
                "(I)Z"));
            }
            else if (t == String.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getStringLocal",
                        "(I)Ljava/lang/String;"));
            }
            else if (t == WeelFunction.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getFunctionLocal",
                        "(I)Lcom/github/rjeschke/weel/WeelFunction;"));
            }
            else if (t == Value.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getValueLocal",
                        "(I)Lcom/github/rjeschke/weel/Value;"));
            }
            else if (t == ValueMap.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getMapLocal",
                        "(I)Lcom/github/rjeschke/weel/ValueMap;"));
            }
            else
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "getObjectLocal",
                        "(I)Ljava/lang/Object;"));
                if (t != Object.class)
                {
                    bl.add(JvmOp.CHECKCAST);
                    bl.addShort(this.classWriter.addConstant(new JvmConstant(
                            JvmConstant.CONSTANT_Class, this.classWriter
                                    .addConstant(new JvmConstant(t
                                            .getCanonicalName().replace('.',
                                                    '/'))))));
                }
            }
        }

        mw.maxStack = Math.max(mw.maxStack, sp);

        bl.add(JvmOp.INVOKESTATIC);
        bl.addShort(this.classWriter.addMethodRefConstant(m.getDeclaringClass()
                .getCanonicalName(), m.getName(), "("
                + JvmClassWriter.buildDescriptor(m.getParameterTypes()) + ")"
                + JvmClassWriter.buildDescriptor(m.getReturnType())));

        if (returns)
        {
            sp = 1;
            final Class<?> t = m.getReturnType();
            bl.add(JvmOp.INVOKEVIRTUAL);
            if (t == double.class)
            {
                sp += 2;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load", "(D)V"));
            }
            else if (t == int.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load", "(I)V"));
            }
            else if (t == boolean.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load", "(Z)V"));
            }
            else if (t == String.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load",
                        "(Ljava/lang/String;)V"));
            }
            else if (t == ValueMap.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load",
                        "(Lcom/github/rjeschke/weel/ValueMap;)V"));
            }
            else if (t == Value.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load",
                        "(Lcom/github/rjeschke/weel/Value;)V"));
            }
            else if (t == WeelFunction.class)
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load",
                        "(Lcom/github/rjeschke/weel/WeelFunction;)V"));
            }
            else
            {
                sp++;
                bl.addShort(this.classWriter.addMethodRefConstant(
                        "com.github.rjeschke.weel.WeelRuntime", "load",
                        "(Ljava/lang/Object;)V"));
            }

            mw.maxStack = Math.max(mw.maxStack, sp);

            bl.add(JvmOp.ALOAD_0);
            mw.ldcInt(1);
            bl.add(JvmOp.INVOKEVIRTUAL);
            bl.addShort(this.classWriter
                    .addMethodRefConstant("com.github.rjeschke.weel.WeelRuntime",
                            "closeFrameRet", "(I)V"));
        }
        else
        {
            bl.add(JvmOp.ALOAD_0);
            mw.ldcInt(0);
            bl.add(JvmOp.INVOKEVIRTUAL);
            bl.addShort(this.classWriter.addMethodRefConstant(
                    "com.github.rjeschke.weel.WeelRuntime", "closeFrame", "(I)V"));
        }
        bl.add(JvmOp.RETURN);

        return mname;
    }
}
