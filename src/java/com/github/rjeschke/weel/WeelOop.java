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
public class WeelOop
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
     * @param runtime The runtime.
     * @param base The base class.
     * @param args The arguments.
     * @return The new object.
     */
    public final static ValueMap newClass(final Runtime runtime, final ValueMap base, final Value ... args)
    {
        final ValueMap clazz = base.clone();
        final Value ctor = clazz.get("ctor");
        final int ac = args.length + 1;
        if (ctor.type == ValueType.FUNCTION)
        {
            WeelFunction f = ctor.function;
            if (f.arguments != ac)
            {
                f = runtime.getMother().findFunction(f.name, ac);
            }
            if (f != null)
            {
                runtime.load(clazz);
                for(int i = 0; i < args.length; i++)
                {
                    runtime.load(args[i]);
                }
                f.invoke(runtime);
                if (f.returnsValue)
                {
                    runtime.pop1();
                }
            }
        }
        return clazz;
    }
}
