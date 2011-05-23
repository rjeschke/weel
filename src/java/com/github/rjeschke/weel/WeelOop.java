/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * Weel Oop utility class.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelOop
{
    /**
     * Constructor.
     */
    private WeelOop()
    {
        //
    }

    /**
     * Sets the Java object instance for a Weel class.
     * 
     * @param thiz
     *            The Weel class.
     * @param instance
     *            The instance.
     */
    public static void setInstance(final ValueMap thiz, final Object instance)
    {
        thiz.set("#INSTANCE#", new Value(instance));
    }

    /**
     * Gets the Java object instance from a Weel class.
     * 
     * @param <T>
     *            The type.
     * @param thiz
     *            The Weel class.
     * @param type
     *            The Type.
     * @return The instance.
     */
    public static <T> T getInstance(final ValueMap thiz, final Class<T> type)
    {
        return type.cast(thiz.get("#INSTANCE#").getObject());
    }

    /**
     * Creates a Weel class object.
     * 
     * @param runtime
     *            The runtime.
     * @param base
     *            The base class.
     * @param args
     *            The arguments.
     * @return The new object.
     */
    public final static ValueMap newClass(final WeelRuntime runtime,
            final ValueMap base, final Value... args)
    {
        final ValueMap clazz = base.clone();
        final Value ctor = clazz.get("ctor");
        final int ac = args.length + 1;
        if (ctor.type == ValueType.FUNCTION)
        {
            WeelFunction f = (WeelFunction)ctor.object;
            if (f.arguments != ac)
            {
                f = runtime.getMother().findFunction(f.name, ac);
            }
            if (f != null)
            {
                runtime.load(clazz);
                for (int i = 0; i < args.length; i++)
                {
                    runtime.load(args[i]);
                }
                f.invoke(runtime);
                if (f.returnsValue)
                {
                    runtime.pop1();
                }
            }
            else
            {
                throw new WeelException("Wrong number of arguments for constructor: " + ctor.toString());
            }
        }
        return clazz;
    }

    /**
     * Gets a member function of 'thiz'.
     * 
     * @param runtime
     *            The runtime.
     * @param thiz
     *            The 'thiz' object.
     * @param name
     *            The name.
     * @param args
     *            Number of arguments.
     * @return The function or <code>null</code>.
     */
    public static WeelFunction getFunction(final WeelRuntime runtime,
            final ValueMap thiz, final String name, final int args)
    {
        WeelFunction func = thiz.get(name).getFunction();
        if (func.arguments != args)
        {
            func = runtime.getMother().findFunction(func.name, args);
        }
        return func;
    }

    /**
     * Performs a 'this' call.
     * 
     * @param runtime
     *            The runtime.
     * @param thiz
     *            The 'thiz' object.
     * @param name
     *            The name.
     * @param args
     *            The arguments.
     * @return Return value.
     */
    public final static Value invoke(final WeelRuntime runtime,
            final ValueMap thiz, final String name, final Value... args)
    {
        final WeelFunction func = getFunction(runtime, thiz, name,
                args.length + 1);
        if (func == null)
        {
            throw new WeelException("Unknown member function '" + name + "("
                    + args.length + ")");
        }
        return runtime.invoke(func, thiz, args);
    }
}
