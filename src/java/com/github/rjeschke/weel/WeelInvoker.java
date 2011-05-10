/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * Weel invocation interface to support both JNI and reflection invocation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
interface WeelInvoker
{
    /**
     * Invokes a Weel function.
     * 
     * @param runtime
     *            The Weel Runtime.
     */
    public void invoke(final Runtime runtime);

    /**
     * Invokes a Weel anonymous function with closure.
     * 
     * @param runtime
     *            The Weel Runtime.
     * @param function
     *            The virtual WeelFunction.
     * 
     */
    public void invoke(final Runtime runtime, final WeelFunction function);

    /**
     * Initializes this invoker.
     * 
     * @param weel
     *            The Weel.
     * @param function
     *            The Weel function.
     */
    public void initialize(final Weel weel, final WeelFunction function);
}
