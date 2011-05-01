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
    /** Virtual function? */
    boolean isVirtual;
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
    /** An Object for non-static method invocation. */
    Object instance;
    /** Invoker for invocation from outside Weel compiled code. */
    WeelInvoker invoker;

    /**
     * Creates a virtual function from this function.
     * 
     * @param runtime
     *            The Weel runtime.
     * @return The cloned virtual function.
     */
    WeelFunction cloneVirtual(final Runtime runtime)
    {
        final WeelFunction func = new WeelFunction();

        func.index = this.index;
        func.name = "ANON";
        func.arguments = this.arguments;
        func.returnsValue = this.returnsValue;
        func.isVirtual = true;
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
                func.environment[i] = var < 0 ? runtime.ginenv(1 - var)
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
        if (this.isVirtual)
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
}
