/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * Weel function descriptor.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
// FIXME Finalize more!
public final class WeelFunction
{
    /** The Weel function index. */
    int index;
    /** The Weel name of this function. */
    String name;
    /** Number of arguments. */
    int arguments;
    /** Does this function return a value? */
    boolean returnsValue;
    /** Function type. */
    @Deprecated
    WeelFunctionType type;

    /** The class name. */
    String clazz;
    /** The java method name. */
    String javaName;
    /** An Object for non-static method invocation. */
    Object instance;
    /** Invoker for invocation from outside Weel compiled code. */
    WeelInvoker invoker;

    /**
     * Calls WeelInvoker.initialize().
     * 
     * @see com.github.rjeschke.weel.WeelInvoker#initialize(WeelFunction)
     */
    void initialize()
    {
        this.invoker.initialize(this);
    }

    /**
     * Invokes this method.
     * 
     * @param runtime
     *            The Weel Runtime.
     * @see com.github.rjeschke.weel.WeelInvoker#invoke(Runtime)
     */
    public void invoke(final Runtime runtime)
    {
        this.invoker.invoke(runtime);
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return (this.returnsValue ? "function " : "sub ") + this.name + "("
                + this.arguments + ")";
    }

    /**
     * Gets this function's name.
     * 
     * @return The name of this function.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets this function's number of arguments.
     * 
     * @return The number of arguments.
     */
    public int getNumArguments()
    {
        return this.arguments;
    }

    /**
     * Checks if this function returns a value.
     * 
     * @return <code>true</code> if this functions returns a value.
     */
    public boolean returnsValue()
    {
        return this.returnsValue;
    }
}
