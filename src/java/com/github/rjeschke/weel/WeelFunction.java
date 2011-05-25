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
public class WeelFunction
{
    /** The Weel function index. */
    int index;
    /** The Weel name of this function. */
    String name;
    /** Number of arguments. */
    int arguments;
    /** Does this function return a value? */
    boolean returnsValue;
    /** Does this function contain a closure? */
    boolean isClosure;
    /** Function parent. */
    WeelFunction parent;
    /** Closure environment. */
    Value[] environment;
    /** Environment indices. */
    int[] envLocals;

    /** The class name. */
    String clazz;
    /** The java method name. */
    String javaName;
    /** Invoker for invocation from outside Weel compiled code. */
    WeelInvoker invoker;
    /** Class loader for runtime compiled functions. */
    WeelLoader loader;
    
    /**
     * Constructor.
     */
    WeelFunction()
    {
        // empty
    }

    /**
     * Creates a closure function from this function.
     * 
     * @param runtime
     *            The Weel runtime.
     * @return The cloned 
     */
    WeelFunction cloneClosure(final WeelRuntime runtime)
    {
        final WeelFunction func = new WeelFunction();

        func.index = this.index;
        func.name = "ANON";
        func.arguments = this.arguments;
        func.returnsValue = this.returnsValue;
        func.isClosure = true;
        func.parent = this;

        // Closure?
        if (this.envLocals != null)
        {
            // Yes, save environment
            func.environment = new Value[this.envLocals.length];
            for (int i = 0; i < this.envLocals.length; i++)
            {
                // We clone here ...
                final int var = this.envLocals[i];
                func.environment[i] = var < 0 ? runtime.ginenv(-var - 1)
                        : runtime.gloc(var);
            }
        }

        return func;
    }

    /**
     * Calls WeelInvoker.initialize().
     * 
     * @see com.github.rjeschke.weel.WeelInvoker#initialize(WeelFunction)
     */
    void initialize(final Weel weel)
    {
        this.invoker.initialize(weel, this);
    }

    /**
     * Invokes this method.
     * 
     * @param runtime
     *            The Weel Runtime.
     * @see com.github.rjeschke.weel.WeelInvoker#invoke(WeelRuntime)
     */
    public void invoke(final WeelRuntime runtime)
    {
        if (this.isClosure)
            this.parent.invoker.invoke(runtime, this);
        else
            this.invoker.invoke(runtime);
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return (this.returnsValue ? "func " : "sub ") + this.name + "("
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

    /**
     * Gets a full textual representation of this function.
     * 
     * @return The full textual representation of this function.
     */
    public String toFullString()
    {
        return this.toString() + " -> void " + this.clazz + "." + this.javaName
                + "(Runtime)";
    }
}
