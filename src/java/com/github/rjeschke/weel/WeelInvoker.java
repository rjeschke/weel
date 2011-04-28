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
     * Initializes this invoker.
     * 
     * @param function
     *            The Weel function.
     */
    public void initialize(final WeelFunction function);
}
