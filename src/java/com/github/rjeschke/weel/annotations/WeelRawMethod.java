/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Weel raw method annotation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WeelRawMethod
{
    /**
     * The name of the Weel function. If none is given, the Java method name
     * will be used.
     */
    String name() default "";

    /** Number of arguments. */
    int args() default 0;

    /** <code>true</code> if this function returns a value. */
    boolean returnsValue() default false;
}
