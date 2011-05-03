/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Simple Weel unit testing framework.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public class WeelUnit
{
    
    @WeelRawMethod(name = "wuassert", args = 1)
    public void wuAssert1(final Runtime runtime)
    {
        System.out.println(runtime.pop());
    }
    
    @WeelRawMethod(name = "wuassert2", args = 2)
    public void wuAssert2(final Runtime runtime)
    {
        System.out.println(runtime.pop());
        System.out.println(runtime.pop());
    }
    

    public static void main(String[] args)
    {
        final Weel weel = new Weel();
        
    }
}
