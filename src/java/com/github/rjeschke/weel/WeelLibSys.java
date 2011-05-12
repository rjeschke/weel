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
        runtime.load(runtime.popSize());
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
     * <code>array(size)</code>
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
        final int size = (int) runtime.popNumber();
        final ValueMap ret = new ValueMap();
        final Value val = new Value();
        for (int i = 0; i < size; i++)
            ret.append(val);
        runtime.load(ret);
    }

    /**
     * <code>array(size, val)</code>
     * <p>
     * Creates an array of the given size and fills it with val.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(name = "array", args = 2, returnsValue = true)
    public final static void array2(final Runtime runtime)
    {
        final Value val = runtime.pop();
        final int size = (int) runtime.popNumber();
        final ValueMap ret = new ValueMap();
        for (int i = 0; i < size; i++)
            ret.append(val);
        runtime.load(ret);
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
        runtime.load(runtime.popType() == ValueType.NULL ? -1 : 0);
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
        runtime.load(runtime.popType() == ValueType.NUMBER ? -1 : 0);
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
        runtime.load(runtime.popType() == ValueType.STRING ? -1 : 0);
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
        runtime.load(runtime.popType() == ValueType.MAP ? -1 : 0);
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
        runtime.load(runtime.popType() == ValueType.FUNCTION ? -1 : 0);
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
        runtime.load(runtime.popType() == ValueType.OBJECT ? -1 : 0);
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
        runtime.load(runtime.popFunction().name);
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
        runtime.load(runtime.popFunction().arguments);
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
        runtime.load(runtime.popFunction().returnsValue ? -1 : 0);
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
        final int args = (int) runtime.popNumber();
        final String name = runtime.popString();
        final WeelFunction func = runtime.getMother().findFunction(name, args);
        if (func != null)
            runtime.load(func);
        else
            runtime.load();
    }

    /**
     * <code>funcreg(type, map)</code>
     * <p>
     * Registers type bound functions.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2)
    public final static void funcReg(final Runtime runtime)
    {
        final ValueMap map = runtime.popMap();
        final String typeName = runtime.popString().toUpperCase();
        final ValueType type = ValueType.fromString(typeName);

        if (type == null)
        {
            throw new WeelException("Unknown type '" + typeName + "'");
        }

        final TypeFunctions funcs = runtime.mother.typeFunctions[type.ordinal()];

        if (funcs == null)
        {
            throw new WeelException("Unsupported type '" + type + "'");
        }

        for (final Entry<Value, Value> e : map)
        {
            final String key = e.getKey().toString().toLowerCase();
            if (e.getValue().isFunction())
            {
                final String name;
                final WeelFunction func = e.getValue().function;
                if (key.endsWith("_"))
                {
                    int i = key.length() - 2;
                    while (i > 0 && key.charAt(i) == '_')
                    {
                        --i;
                    }
                    name = key.substring(0, i + 1) + "#" + func.arguments;
                }
                else
                {
                    name = key + "#" + func.arguments;
                }
                if (funcs.findFunction(name) != null)
                {
                    throw new WeelException("Duplicate function '" + key + "("
                            + func.arguments + ")' for type '" + type + "'");
                }
                funcs.addFunction(name, func);
            }
        }
    }

    /**
     * <code>funcCheck(func, args, returnsValue)</code>
     * <p>
     * Returns true if 'func' is a sub/function (depends on 'returnsValue')
     * taking 'args' arguments .
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void funcCheck(final Runtime runtime)
    {
        final boolean ret = runtime.popBoolean();
        final int args = (int) runtime.popNumber();
        final Value val = runtime.pop();
        
        runtime.load(val.type == ValueType.FUNCTION && val.function.returnsValue == ret && val.function.arguments == args);
    }

    /**
     * <code>funcreg(type, name, func)</code>
     * <p>
     * Registers type bound functions.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(name = "funcReg", args = 3)
    public final static void funcReg3(final Runtime runtime)
    {
        final WeelFunction func = runtime.popFunction();
        final String name = runtime.popString();
        final String iname = name + "#" + func.arguments;
        final String typeName = runtime.popString().toUpperCase();

        final ValueType type = ValueType.fromString(typeName);
        if (type == null)
        {
            throw new WeelException("Unknown type '" + typeName + "'");
        }

        final TypeFunctions funcs = runtime.mother.typeFunctions[type.ordinal()];
        if (funcs == null)
        {
            throw new WeelException("Unsupported type '" + type + "'");
        }

        if (funcs.findFunction(iname) != null)
        {
            throw new WeelException("Duplicate function '" + name + "("
                    + func.arguments + ")' for type '" + type + "'");
        }
        funcs.addFunction(iname, func);
    }

    /**
     * <code>toNum(s)</code>
     * <p>
     * Returns the string argument as a number.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void toNum(final Runtime runtime)
    {
        final String str = runtime.popString().toLowerCase();
        if (str.length() > 2)
        {
            if (str.startsWith("0b"))
                runtime.load(Integer.parseInt(str.substring(2), 2));
            else if (str.startsWith("0o"))
                runtime.load(Integer.parseInt(str.substring(2), 8));
            else if (str.startsWith("0x"))
                runtime.load(Integer.parseInt(str.substring(2), 16));
            else
                runtime.load(Double.parseDouble(str));
        }
        else
            runtime.load(Double.parseDouble(str));
    }

    /**
     * <code>toStr(v)</code>
     * <p>
     * Returns the argument as a string.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void toStr(final Runtime runtime)
    {
        runtime.load(runtime.pop().toString());
    }

    /**
     * <code>typeOf(v)</code>
     * <p>
     * Returns the argument's type as a string.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void typeOf(final Runtime runtime)
    {
        runtime.load(runtime.pop().type.toString());
    }
}
