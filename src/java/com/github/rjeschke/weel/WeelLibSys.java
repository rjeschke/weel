/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.Map.Entry;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel system library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibSys
{
    private WeelLibSys()
    {
        // empty
    }

    /**
     * <code>size(a)</code>
     * <p>
     * Returns the size of a value.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void size(final Runtime runtime)
    {
        final Value v = runtime.pop();
        switch (v.type)
        {
        case NUMBER:
            runtime.load(v.number);
            break;
        case STRING:
            runtime.load(v.string.length());
            break;
        case MAP:
            runtime.load(v.map.size());
            break;
        case FUNCTION:
            runtime.load(v.function.arguments);
            break;
        default:
            runtime.load(0);
            break;
        }
    }

    /**
     * <code>clock()</code>
     * <p>
     * Returns the current value of the most precise available system timer, in
     * seconds.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.System#nanoTime()
     */
    @WeelRawMethod(returnsValue = true)
    public final static void clock(final Runtime runtime)
    {
        runtime.load(System.nanoTime() * 1e-9);
    }

    /**
     * <code>array(a)</code>
     * <p>
     * Creates an array of the given size.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void array(final Runtime runtime)
    {
        runtime.load(new ValueMap((int) runtime.pop().getNumber()));
    }

    /**
     * <code>isNull(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is null.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isNull(final Runtime runtime)
    {
        runtime.load(runtime.pop().type == ValueType.NULL ? -1 : 0);
    }

    /**
     * <code>isNumber(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is a number.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isNumber(final Runtime runtime)
    {
        runtime.load(runtime.pop().type == ValueType.NUMBER ? -1 : 0);
    }

    /**
     * <code>isString(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is a string.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isString(final Runtime runtime)
    {
        runtime.load(runtime.pop().type == ValueType.STRING ? -1 : 0);
    }

    /**
     * <code>isMap(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is a map.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isMap(final Runtime runtime)
    {
        runtime.load(runtime.pop().type == ValueType.MAP ? -1 : 0);
    }

    /**
     * <code>isFunction(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is a function.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isFunc(final Runtime runtime)
    {
        runtime.load(runtime.pop().type == ValueType.FUNCTION ? -1 : 0);
    }

    /**
     * <code>isObject(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is an object.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isObject(final Runtime runtime)
    {
        runtime.load(runtime.pop().type == ValueType.OBJECT ? -1 : 0);
    }

    /**
     * <code>funcName(func)</code>
     * <p>
     * Returns the name of the supplied function.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void funcName(final Runtime runtime)
    {
        runtime.load(runtime.pop().getFunction().name);
    }

    /**
     * <code>funcArgs(func)</code>
     * <p>
     * Returns the number of arguments of the supplied function.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void funcArgs(final Runtime runtime)
    {
        runtime.load(runtime.pop().getFunction().arguments);
    }

    /**
     * <code>funcReturns(func)</code>
     * <p>
     * Returns <code>true</code> if the supplied functions returns a value.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void funcReturns(final Runtime runtime)
    {
        runtime.load(runtime.pop().getFunction().returnsValue ? -1 : 0);
    }

    /**
     * <code>funcFind(name, args)</code>
     * <p>
     * Returns the function with the specified name and number of arguments,
     * <code>null</code> if non could be found.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void funcFind(final Runtime runtime)
    {
        final int args = (int) runtime.pop().getNumber();
        final String name = runtime.pop().toString();
        final WeelFunction func = runtime.getMother().findFunction(name, args);
        if (func != null)
            runtime.load(func);
        else
            runtime.load();
    }

    /**
     * <code>funcreg(type, map)</code>
     * <p>
     * Registers type support functions.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2)
    public final static void funcReg(final Runtime runtime)
    {
        final ValueMap map = runtime.pop().getMap();
        final String typeName = runtime.pop().toString().toUpperCase();
        final ValueType type = ValueType.fromString(typeName);

        if (type == null)
        {
            throw new WeelException("Unknown type '" + typeName + "'");
        }

        final SupportFunctions funcs = runtime.mother.supportFunctions[type
                .ordinal()];

        if (funcs == null)
        {
            throw new WeelException("Unsupported type '" + type + "'");
        }

        for (final Entry<Value, Value> e : map)
        {
            final String key = e.getKey().toString().toLowerCase();
            if (e.getValue().isFunction())
            {
                final WeelFunction func = e.getValue().function;
                funcs.addFunction(key + "#" + func.arguments, func);
            }
        }
    }
}
