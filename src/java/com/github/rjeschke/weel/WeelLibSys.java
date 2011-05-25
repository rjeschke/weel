/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import com.github.rjeschke.weel.ValueMap.ValueMapIterator;
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
    public final static void size(final WeelRuntime runtime)
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
    public final static void clock(final WeelRuntime runtime)
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
    public final static void array(final WeelRuntime runtime)
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
    public final static void array2(final WeelRuntime runtime)
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
    public final static void isNull(final WeelRuntime runtime)
    {
        runtime.load(runtime.popType() == ValueType.NULL ? -1 : 0);
    }

    /**
     * <code>isNotNull(a)</code>
     * <p>
     * Returns <code>true</code> if 'a' is not null.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void isNotNull(final WeelRuntime runtime)
    {
        runtime.load(runtime.popType() != ValueType.NULL ? -1 : 0);
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
    public final static void isNumber(final WeelRuntime runtime)
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
    public final static void isString(final WeelRuntime runtime)
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
    public final static void isMap(final WeelRuntime runtime)
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
    public final static void isFunc(final WeelRuntime runtime)
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
    public final static void isObject(final WeelRuntime runtime)
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
    public final static void funcName(final WeelRuntime runtime)
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
    public final static void funcArgs(final WeelRuntime runtime)
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
    public final static void funcReturns(final WeelRuntime runtime)
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
    public final static void funcFind(final WeelRuntime runtime)
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
    public final static void funcReg(final WeelRuntime runtime)
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

        final Value vkey = new Value();
        final Value val = new Value();
        final ValueMapIterator it = map.createIterator();

        while (it.next(vkey, val))
        {
            final String key = vkey.toString().toLowerCase();
            if (val.isFunction())
            {
                final String name;
                final WeelFunction func = (WeelFunction) val.object;
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
    public final static void funcCheck(final WeelRuntime runtime)
    {
        final boolean ret = runtime.popBoolean();
        final int args = (int) runtime.popNumber();
        final Value val = runtime.pop();

        runtime.load(val.type == ValueType.FUNCTION
                && ((WeelFunction) val.object).returnsValue == ret
                && ((WeelFunction) val.object).arguments == args);
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
    public final static void funcReg3(final WeelRuntime runtime)
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
    public final static void toNum(final WeelRuntime runtime)
    {
        final Value val = runtime.pop();
        if (val.type != ValueType.STRING)
        {
            runtime.load(0);
        }
        else
        {
            try
            {
                final String str = ((String) val.object).toLowerCase();
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
            catch (NumberFormatException e)
            {
                runtime.load(0);
            }
        }
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
    public final static void toStr(final WeelRuntime runtime)
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
    public final static void typeOf(final WeelRuntime runtime)
    {
        runtime.load(runtime.pop().type.toString());
    }

    /**
     * <code>sleep(ms)</code>
     * <p>
     * Sleeps for ne given number of milliseconds.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void sleep(final WeelRuntime runtime)
    {
        try
        {
            Thread.sleep((long) runtime.popNumber());
            runtime.load(true);
        }
        catch (InterruptedException e)
        {
            runtime.load(false);
        }
    }

    /**
     * Returns the amount of memory used by this virtual machine in bytes.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(returnsValue = true)
    public final static void usedMem(final WeelRuntime runtime)
    {
        runtime.load(Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory());
    }

    /**
     * <code>compile(str)</code>
     * <p>
     * Compiles a Weel function contained in 'str' and returns it. Runtime
     * compiled functions are designed to get garbage collected if there are no
     * more references pointing at them.
     * </p>
     * <p>
     * Remember: Compiled script classes use a long counter to avoid naming
     * conflicts (the long counter will overflow and wrap after
     * 9,223,372,036,854,775,809 compiled scripts).
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void compile(final WeelRuntime runtime)
    {
        final String code = runtime.popString();
        Compiler compiler = new Compiler(runtime.getMother());
        runtime.load(compiler.compileFunction(code));
    }

    @WeelRawMethod(args = 1)
    public final static void error(final WeelRuntime runtime)
    {
        throw new WeelException(runtime.pop());
    }

    /**
     * Calls a Weel function with error catching.
     * 
     * @param runtime
     *            The runtime.
     * @param argc
     *            Number of arguments.
     */
    private final static void pcall(final WeelRuntime runtime, final int argc)
    {
        final ValueMap ret = new ValueMap();
        final int sp = runtime.getStackPointer();
        final int fp = runtime.fp;
        try
        {
            runtime.stackCall(argc, true);
            ret.append(new Value(-1));
            ret.append(runtime.pop());
        }
        catch (Exception e)
        {
            runtime.npop(runtime.getStackPointer() - sp + argc + 1);
            runtime.fp = fp;
            ret.append(new Value(0));
            if (e instanceof WeelException)
            {
                final WeelException ex = (WeelException) e;
                final Value v = ex.getValue();
                ret.append(v != null ? v : new Value(e.toString()));
            }
            else
            {
                ret.append(new Value(e.toString()));
            }
        }
        runtime.load(ret);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 1, returnsValue = true)
    public final static void pcall1(final WeelRuntime runtime)
    {
        pcall(runtime, 0);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 2, returnsValue = true)
    public final static void pcall2(final WeelRuntime runtime)
    {
        System.out.println(runtime.getStackPointer());
        pcall(runtime, 1);
        System.out.println(runtime.getStackPointer());
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 3, returnsValue = true)
    public final static void pcall3(final WeelRuntime runtime)
    {
        pcall(runtime, 2);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 4, returnsValue = true)
    public final static void pcall4(final WeelRuntime runtime)
    {
        pcall(runtime, 3);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 5, returnsValue = true)
    public final static void pcall5(final WeelRuntime runtime)
    {
        pcall(runtime, 4);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 6, returnsValue = true)
    public final static void pcall6(final WeelRuntime runtime)
    {
        pcall(runtime, 5);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 7, returnsValue = true)
    public final static void pcall7(final WeelRuntime runtime)
    {
        pcall(runtime, 6);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 8, returnsValue = true)
    public final static void pcall8(final WeelRuntime runtime)
    {
        pcall(runtime, 7);
    }

    /**
     * Protected call.
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "pcall", args = 9, returnsValue = true)
    public final static void pcall9(final WeelRuntime runtime)
    {
        pcall(runtime, 8);
    }
}
