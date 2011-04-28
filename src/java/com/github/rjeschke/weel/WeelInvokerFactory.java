/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * WeelInvoker factory.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class WeelInvokerFactory
{
    /**
     * Currently returns the default invoker only.
     * 
     * @return Default WeelInvoker.
     */
    public final static WeelInvoker create()
    {
        return new WeelReflectionInvoker();
    }
}
