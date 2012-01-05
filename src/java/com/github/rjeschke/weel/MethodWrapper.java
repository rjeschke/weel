/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        for(int i = 0; i < m.getParameterTypes().length; i++)
        {
            if(m.getParameterTypes()[i] == com.github.rjeschke.weel.WeelRuntime.class)
                func.arguments--;
        }

        final String mname = "wrap$" + m.getName() + "$" + func.arguments;
        final JvmMethodWriter mw = this.classWriter.createMethod(mname,
                "(Lcom/github/rjeschke/weel/WeelRuntime;)V");

        mw.aload(0);
        mw.ldc(func.arguments);
        mw.ldc(0);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "openFrame",
                "(II)V");

        final boolean returns;

        if(m.getReturnType() != void.class)
        {
            returns = true;
            mw.aload(0);
        }
        else
        {
            returns = false;
        }

        for(int i = 0, p = 0; i < m.getParameterTypes().length; i++)
        {
            final Class<?> t = m.getParameterTypes()[i];
            if(t == com.github.rjeschke.weel.WeelRuntime.class)
            {
                mw.aload(0);
                continue;
            }
            mw.aload(0);
            mw.ldc(p++);
            if(t == double.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getNumberLocal", "(I)D");
            }
            else if(t == int.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getNumberLocal", "(I)D");
                mw.addOp(JvmOp.D2I);
                mw.curStack--;
            }
            else if(t == boolean.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getBooleanLocal", "(I)Z");
            }
            else if(t == String.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getStringLocal", "(I)Ljava/lang/String;");
            }
            else if(t == WeelFunction.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getFunctionLocal",
                        "(I)Lcom/github/rjeschke/weel/WeelFunction;");
            }
            else if(t == Value.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getValueLocal", "(I)Lcom/github/rjeschke/weel/Value;");
            }
            else if(t == ValueMap.class)
            {
                mw
                        .invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                                "getMapLocal",
                                "(I)Lcom/github/rjeschke/weel/ValueMap;");
            }
            else
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "getObjectLocal", "(I)Ljava/lang/Object;");
                if(t != Object.class)
                {
                    mw.addOp(JvmOp.CHECKCAST);
                    mw.code.addShort(this.classWriter
                            .addConstant(new JvmConstant(
                                    JvmConstant.CONSTANT_Class,
                                    this.classWriter
                                            .addConstant(new JvmConstant(t
                                                    .getCanonicalName()
                                                    .replace('.', '/'))))));
                }
            }
        }

        mw.invokeStatic(m.getDeclaringClass().getCanonicalName(), m.getName(),
                "(" + JvmClassWriter.buildDescriptor(m.getParameterTypes())
                        + ")"
                        + JvmClassWriter.buildDescriptor(m.getReturnType()));

        if(returns)
        {
            final Class<?> t = m.getReturnType();
            if(t == double.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(D)V");
            }
            else if(t == int.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(I)V");
            }
            else if(t == boolean.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(Z)V");
            }
            else if(t == String.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(Ljava/lang/String;)V");
            }
            else if(t == ValueMap.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(Lcom/github/rjeschke/weel/ValueMap;)V");
            }
            else if(t == Value.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(Lcom/github/rjeschke/weel/Value;)V");
            }
            else if(t == WeelFunction.class)
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(Lcom/github/rjeschke/weel/WeelFunction;)V");
            }
            else
            {
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                        "load", "(Ljava/lang/Object;)V");
            }

            mw.aload(0);
            mw.ldc(1);
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                    "closeFrameRet", "(I)V");
        }
        else
        {
            mw.aload(0);
            mw.ldc(0);
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime",
                    "closeFrame", "(I)V");
        }
        mw.addOp(JvmOp.RETURN);

        return mname;
    }
}
